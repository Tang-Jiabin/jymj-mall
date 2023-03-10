package com.jymj.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.MdseConstants;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.enums.CouponStateEnum;
import com.jymj.mall.common.enums.CouponTypeEnum;
import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.redis.RedissonLockUtil;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.marketing.api.CouponFeignClient;
import com.jymj.mall.marketing.dto.UserCouponDTO;
import com.jymj.mall.marketing.vo.UserCouponInfo;
import com.jymj.mall.mdse.api.MdseFeignClient;
import com.jymj.mall.mdse.api.MdseStockFeignClient;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdseInfoShow;
import com.jymj.mall.mdse.dto.MdsePurchaseRecordDTO;
import com.jymj.mall.mdse.dto.StockDTO;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import com.jymj.mall.mdse.vo.EffectiveRulesInfo;
import com.jymj.mall.mdse.vo.MdseInfo;
import com.jymj.mall.mdse.vo.PictureInfo;
import com.jymj.mall.mdse.vo.StockInfo;
import com.jymj.mall.order.dto.OrderDTO;
import com.jymj.mall.order.dto.OrderMdseDTO;
import com.jymj.mall.order.dto.OrderPageQuery;
import com.jymj.mall.order.dto.OrderPaySuccess;
import com.jymj.mall.order.entity.MallOrder;
import com.jymj.mall.order.entity.MallOrderDeliveryDetails;
import com.jymj.mall.order.entity.MallOrderMdseDetails;
import com.jymj.mall.order.repository.MallOrderDeliveryDetailsRepository;
import com.jymj.mall.order.repository.MallOrderMdseDetailsRepository;
import com.jymj.mall.order.repository.MallOrderRepository;
import com.jymj.mall.order.service.OrderService;
import com.jymj.mall.order.vo.MallOrderDeliveryDetailsInfo;
import com.jymj.mall.order.vo.MallOrderInfo;
import com.jymj.mall.order.vo.MallOrderMdseDetailsInfo;
import com.jymj.mall.shop.dto.VerifyOrderMdse;
import com.jymj.mall.shop.vo.ShopInfo;
import com.jymj.mall.user.api.UserAddressFeignClient;
import com.jymj.mall.user.api.UserFeignClient;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.vo.AddressInfo;
import com.jymj.mall.user.vo.MemberInfo;
import com.jymj.mall.user.vo.UserInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Sets;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ??????
 *
 * @author ?????????
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MallOrderServiceImpl implements OrderService {


    private final MdseFeignClient mdseFeignClient;
    private final UserFeignClient userFeignClient;
    private final RedissonLockUtil redissonLockUtil;
    private final MdseStockFeignClient mdseStockFeignClient;
    private final UserAddressFeignClient addressFeignClient;
    private final CouponFeignClient couponFeignClient;
    private final MallOrderRepository orderRepository;
    private final ThreadPoolTaskExecutor executor;
    private final MallOrderMdseDetailsRepository orderMdseDetailsRepository;
    private final MallOrderDeliveryDetailsRepository orderDeliveryDetailsRepository;

    /**
     * ????????????
     *
     * @param dto ????????????
     * @return ????????????
     */
    @Override
    @GlobalTransactional(name = "mall-order-add", rollbackFor = Exception.class)
    public MallOrder add(OrderDTO dto) {

        Long userId = UserUtils.getUserId();
        Long addressId = dto.getAddressId();
        List<OrderMdseDTO> orderMdseList = dto.getOrderMdseList();
        OrderDeliveryMethodEnum orderDeliveryMethod = dto.getOrderDeliveryMethod();
        Assert.notNull(orderDeliveryMethod, "????????????????????????");
        Assert.notNull(orderMdseList, "??????????????????");
        //????????????????????????
        MallOrder mallOrder = saveOrderBaseInfo(dto, userId, orderDeliveryMethod);
        //??????????????????????????????
        MallOrderDeliveryDetails mallOrderDeliveryDetails = saveOrderAddressInfo(addressId, orderDeliveryMethod, mallOrder.getOrderId());
        mallOrder.setOrderDeliveryDetailsId(mallOrderDeliveryDetails.getOrderDeliveryDetailsId());
        //????????????????????????
        List<MallOrderMdseDetails> orderMdseDetailsList = saveOrderMdseDetailsList(orderMdseList, mallOrder.getOrderId());
        //???????????????
        BigDecimal totalAmount = orderMdseDetailsList.stream()
                .map(mdseDetail -> mdseDetail.getMdsePrice().multiply(new BigDecimal(String.valueOf(mdseDetail.getQuantity()))).setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        mallOrder.setTotalAmount(totalAmount);
        //????????????
        BigDecimal payableAmount = totalAmount;
        //????????????
        BigDecimal freightAmount = BigDecimal.ZERO;

        //??????????????????
        List<Long> couponIdList = dto.getCouponIdList();
        List<Long> promotionIdList = dto.getPromotionIdList();
        List<UserCouponInfo> userCouponInfoList = getUserCouponInfoList(couponIdList);
        //???????????????
        Map<CouponTypeEnum, UserCouponInfo> couponMap = checkCoupon(userCouponInfoList);
        //???????????????
        payableAmount = checkFullReductionCoupon(orderMdseDetailsList, payableAmount, couponMap);
        //???????????????
        payableAmount = checkDiscountCoupon(orderMdseDetailsList, payableAmount, couponMap);
        //???????????????
        payableAmount = checkCashCoupon(orderMdseDetailsList, payableAmount, couponMap);
        //???????????????
        List<MallOrderMdseDetails> exchangeMdseDetailsList = checkExchangeCoupon(orderMdseDetailsList, payableAmount, couponMap);
        //???????????????
        List<MallOrderMdseDetails> presentMdseDetailsList = checkFullPresentCoupon(orderMdseDetailsList, payableAmount, couponMap);
        //???????????????
        freightAmount = checkFreeShippingCoupon(orderMdseDetailsList, freightAmount, couponMap);
        mallOrder.setOrderCouponIds(userCouponInfoList.stream().map(UserCouponInfo::getCouponId).map(String::valueOf).collect(Collectors.joining(",")));

        //???????????? = ??????????????? - ???????????? + ????????????
        mallOrder.setAmountPayable(payableAmount.add(freightAmount));

        return orderRepository.save(mallOrder);
    }

    /**
     * ????????????
     *
     * @param dto ??????dto
     * @return ????????????
     */
    @Override
    @GlobalTransactional(name = "mall-order-update", rollbackFor = Exception.class)
    @CacheEvict(value = "mall-order:order-info:", key = "'order-id:'+#dto.orderId")
    public Optional<MallOrder> update(OrderDTO dto) {
        if (!ObjectUtils.isEmpty(dto)) {
            if (ObjectUtils.isEmpty(dto.getOrderId()) && ObjectUtils.isEmpty(dto.getOrderNo())) {
                throw new BusinessException("???????????????");
            }

            Optional<MallOrder> mallOrderOptional = ObjectUtils.isEmpty(dto.getOrderId()) ? findByOrderNo(dto.getOrderNo()) : findById(dto.getOrderId());

            MallOrder mallOrder = mallOrderOptional.orElseThrow(() -> new BusinessException("???????????????"));
            boolean update = false;
            if (!ObjectUtils.isEmpty(dto.getStatusEnum())) {
                updateOrderStatus(dto.getStatusEnum(), mallOrder);
                mallOrder.setOrderStatus(dto.getStatusEnum());
                update = true;
            }
            if (!ObjectUtils.isEmpty(dto.getAddressId())) {
                updateOrderAddress(dto.getAddressId(), mallOrder);
            }
            if (StringUtils.hasText(dto.getRemarks())) {
                mallOrder.setRemarks(dto.getRemarks());
                update = true;
            }
            if (update) {
                return Optional.of(orderRepository.save(mallOrder));
            }

        }
        return Optional.empty();
    }

    /**
     * ??????????????????
     *
     * @param addressId ????????????id
     * @param mallOrder ????????????
     */
    private void updateOrderAddress(Long addressId, MallOrder mallOrder) {
        Result<AddressInfo> addressInfoResult = addressFeignClient.getAddressById(addressId);
        if (!Result.isSuccess(addressInfoResult)) {
            throw new BusinessException("???????????????");
        }
        AddressInfo addressInfo = addressInfoResult.getData();
        Optional<MallOrderDeliveryDetails> deliveryDetailsOptional = orderDeliveryDetailsRepository.findById(mallOrder.getOrderDeliveryDetailsId());
        MallOrderDeliveryDetails orderDeliveryDetails = deliveryDetailsOptional.orElseThrow(() -> new BusinessException("??????????????????"));
        orderDeliveryDetails.setAddressee(addressInfo.getName());
        orderDeliveryDetails.setMobile(addressInfo.getMobile());
        orderDeliveryDetails.setAddressId(addressId);
        orderDeliveryDetails.setDetailedAddress(addressInfo.getRegion() + " " + addressInfo.getDetailedAddress());
        orderDeliveryDetails.setOrderDeliveryMethod(OrderDeliveryMethodEnum.EXPRESS);
        orderDeliveryDetailsRepository.save(orderDeliveryDetails);
    }

    /**
     * ??????????????????
     *
     * @param statusEnum ????????????
     * @param mallOrder  ????????????
     */
    private void updateOrderStatus(OrderStatusEnum statusEnum, MallOrder mallOrder) {
        switch (statusEnum) {
            case UNPAID:
                //?????????
                break;
            case UNSHIPPED:
                //?????????
                break;
            case UNRECEIVED:
                //?????????
                break;
            case COMPLETED:
                //?????????
                break;
            case CLOSED:
                //??????
            case CANCELED:
                //??????
                updateOrderStatusClosedOrCanceled(statusEnum, mallOrder);
                break;
            case AFTER_SALES:
                //??????
                break;
        }



    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param statusEnum ????????????
     * @param mallOrder  ????????????
     */
    private void updateOrderStatusClosedOrCanceled(OrderStatusEnum statusEnum, MallOrder mallOrder) {
        if (statusEnum == OrderStatusEnum.CANCELED || statusEnum == OrderStatusEnum.CLOSED) {
            List<MallOrderMdseDetails> mdseDetailsList = orderMdseDetailsRepository.findAllByOrderId(mallOrder.getOrderId());
            for (MallOrderMdseDetails orderMdseDetails : mdseDetailsList) {
                if (mallOrder.getOrderStatus() == OrderStatusEnum.UNPAID && orderMdseDetails.getInventoryReductionMethod() == InventoryReductionMethod.CREATE_ORDER && orderMdseDetails.getType() == 1) {
                    Long stockId = orderMdseDetails.getStockId();
                    Integer quantity = orderMdseDetails.getQuantity();
                    mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(-quantity).build());
                    mdseFeignClient.updateMdseSalesVolume(MdseDTO.builder().mdseId(orderMdseDetails.getMdseId()).salesVolume(-quantity).build());
                }
                if (mallOrder.getOrderStatus() == OrderStatusEnum.UNSHIPPED && orderMdseDetails.getType().equals(MdseConstants.MDSE_TYPE_MDSE)) {
                    Long stockId = orderMdseDetails.getStockId();
                    Integer quantity = orderMdseDetails.getQuantity();
                    mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(-quantity).build());
                    mdseFeignClient.updateMdseSalesVolume(MdseDTO.builder().mdseId(orderMdseDetails.getMdseId()).salesVolume(-quantity).build());
                }
            }
        }
    }

    /**
     * ????????????
     *
     * @param ids id?????? ??????????????? ??????,??????
     */
    @Override
    @CacheEvict(value = "mall-order:order-info:", allEntries = true)
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MallOrder> orderList = orderRepository.findAllById(idList);
            orderList = orderList.stream().filter(order -> order.getUserId().equals(UserUtils.getUserId())).collect(Collectors.toList());
            orderRepository.deleteAll(orderList);
        }
    }

    /**
     * ??????id????????????
     *
     * @param id id
     * @return ????????????
     */
    @Override
    public Optional<MallOrder> findById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * ?????????VO
     *
     * @param entity ??????
     * @return ??????vo??????
     */
    @Override
    public MallOrderInfo entity2vo(MallOrder entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            MallOrderInfo orderInfo = new MallOrderInfo();
            orderInfo.setOrderId(entity.getOrderId());
            orderInfo.setOrderNo(entity.getOrderNo());
            orderInfo.setOrderStatus(entity.getOrderStatus());
            orderInfo.setTotalAmount(entity.getTotalAmount());
            orderInfo.setAmountPayable(entity.getAmountPayable());
            orderInfo.setAmountActuallyPaid(entity.getAmountActuallyPaid());
            orderInfo.setOrderPayMethod(entity.getOrderPayMethod());
            orderInfo.setPayTime(entity.getPayTime());
            orderInfo.setOrderDeliveryMethod(entity.getOrderDeliveryMethod());
            orderInfo.setCreateTime(entity.getCreateTime());
            orderInfo.setDeliveryTime(entity.getDeliveryTime());
            orderInfo.setReceivingTime(entity.getReceivingTime());
            orderInfo.setRemarks(entity.getRemarks());
            //??????????????????
            List<MallOrderMdseDetails> orderMdseDetailsList = orderMdseDetailsRepository.findAllByOrderId(entity.getOrderId());
            if (!CollectionUtils.isEmpty(orderMdseDetailsList)) {
                List<MallOrderMdseDetailsInfo> orderMdseDetailsInfoList = orderMdseDetailsList2vo(orderMdseDetailsList);
                orderInfo.setOrderMdseDetailsInfoList(orderMdseDetailsInfoList);
            }

            //????????????????????????
            if (!ObjectUtils.isEmpty(entity.getOrderDeliveryDetailsId())) {
                Optional<MallOrderDeliveryDetails> orderDeliveryDetailsOptional = orderDeliveryDetailsRepository.findById(entity.getOrderDeliveryDetailsId());
                orderDeliveryDetailsOptional.ifPresent(orderDeliveryDetails -> {
                    MallOrderDeliveryDetailsInfo orderDeliveryDetailsInfo = new MallOrderDeliveryDetailsInfo();
                    orderDeliveryDetailsInfo.setOrderDeliveryMethod(OrderDeliveryMethodEnum.EXPRESS);
                    orderDeliveryDetailsInfo.setAddressId(orderDeliveryDetails.getAddressId());
                    orderDeliveryDetailsInfo.setAddressee(orderDeliveryDetails.getAddressee());
                    orderDeliveryDetailsInfo.setMobile(orderDeliveryDetails.getMobile());
                    orderDeliveryDetailsInfo.setDetailedAddress(orderDeliveryDetails.getDetailedAddress());
                    orderInfo.setOrderDeliveryDetailsInfo(orderDeliveryDetailsInfo);
                });
            }
            //??????????????????
            Result<UserInfo> userInfoResult = userFeignClient.getUserById(entity.getUserId());

            if (Result.isSuccess(userInfoResult)) {
                UserInfo userInfo = userInfoResult.getData();
                orderInfo.setUserInfo(userInfo);
            }

            return orderInfo;
        }
        return null;
    }

    /**
     * ???????????????????????????VO
     *
     * @param orderMdseDetailsList ????????????????????????
     * @return ??????????????????
     */
    private List<MallOrderMdseDetailsInfo> orderMdseDetailsList2vo(List<MallOrderMdseDetails> orderMdseDetailsList) {

        List<CompletableFuture<MallOrderMdseDetailsInfo>> futureList = Optional.of(orderMdseDetailsList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.getCardId().equals(0L))
                .map(entity -> CompletableFuture.supplyAsync(() -> entity.getType().equals(MdseConstants.MDSE_TYPE_MDSE) ? orderMdseDetails2vo(entity) : orderCardDetails2vo(entity, orderMdseDetailsList), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


    }

    /**
     * ???????????????
     *
     * @param orderMdseDetails     ??????????????????
     * @param orderMdseDetailsList ????????????????????????
     * @return ??????????????????
     */
    private MallOrderMdseDetailsInfo orderCardDetails2vo(MallOrderMdseDetails orderMdseDetails, List<MallOrderMdseDetails> orderMdseDetailsList) {
        MallOrderMdseDetailsInfo info = orderMdseDetails2vo(orderMdseDetails);
        info.setCardMdseInfoList(orderMdseDetailsList
                .stream()
                .filter(details -> details.getCardId().equals(orderMdseDetails.getMdseId()))
                .map(this::orderMdseDetails2vo)
                .collect(Collectors.toList()));
        return info;
    }

    /**
     * ?????????????????????VO
     *
     * @param mallOrderMdseDetails ??????????????????
     * @return ????????????
     */
    private MallOrderMdseDetailsInfo orderMdseDetails2vo(MallOrderMdseDetails mallOrderMdseDetails) {
        MallOrderMdseDetailsInfo info = new MallOrderMdseDetailsInfo();
        if (Objects.nonNull(mallOrderMdseDetails)) {
            info.setMdseId(mallOrderMdseDetails.getMdseId());
            info.setStockId(mallOrderMdseDetails.getStockId());
            info.setShopId(mallOrderMdseDetails.getShopId());
            info.setQuantity(mallOrderMdseDetails.getQuantity());
            info.setType(mallOrderMdseDetails.getType());
            info.setShopName(mallOrderMdseDetails.getShopName());
            info.setMdseName(mallOrderMdseDetails.getMdseName());
            info.setNumber(mallOrderMdseDetails.getNumber());
            info.setMdseStockSpec(mallOrderMdseDetails.getMdseStockSpec());
            info.setUsageStatus(mallOrderMdseDetails.getUsageStatus());
            info.setUsageQuantity(Objects.nonNull(mallOrderMdseDetails.getUsageQuantity()) ? mallOrderMdseDetails.getUsageQuantity() : 0);
            info.setUsageDate(mallOrderMdseDetails.getUsageDate());
            info.setMdsePicture(mallOrderMdseDetails.getMdsePicture());
            info.setMdsePrice(mallOrderMdseDetails.getMdsePrice());
            if (mallOrderMdseDetails.getType().equals(MdseConstants.MDSE_TYPE_CARD)) {
                Result<EffectiveRulesInfo> effectiveRulesInfoResult = mdseFeignClient.getCardRulesByMdseId(mallOrderMdseDetails.getMdseId());
                if (Result.isSuccess(effectiveRulesInfoResult)) {
                    EffectiveRulesInfo effectiveRules = effectiveRulesInfoResult.getData();
                    info.setEffectiveRules(effectiveRules);
                }
            }
        }

        return info;
    }

    /**
     * ???????????????VO
     *
     * @param entityList ????????????
     * @return VO
     */
    @Override
    public List<MallOrderInfo> list2vo(List<MallOrder> entityList) {
        List<CompletableFuture<MallOrderInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(Objects::nonNull)
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * ????????????
     *
     * @param orderPageQuery ????????????
     * @return ????????????
     */
    @Override
    public Page<MallOrder> findPage(OrderPageQuery orderPageQuery) {


        Pageable pageable = PageUtils.getPageable(orderPageQuery);
        Specification<MallOrder> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();
            Set<Long> orderIdList = Sets.newHashSet();

            if (!ObjectUtils.isEmpty(orderPageQuery.getUserId())) {
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), orderPageQuery.getUserId()));
            }

            if (!ObjectUtils.isEmpty(orderPageQuery.getOrderStatus())) {
                list.add(criteriaBuilder.equal(root.get("orderStatus").as(OrderStatusEnum.class), orderPageQuery.getOrderStatus()));
            }

            if (!ObjectUtils.isEmpty(orderPageQuery.getShopIdList())) {
                List<MallOrderMdseDetails> orderMdseDetailsList = orderMdseDetailsRepository.findAllByShopIdIn(orderPageQuery.getShopIdList());
                orderIdList.addAll(orderMdseDetailsList.stream().map(MallOrderMdseDetails::getOrderId).filter(orderId -> !ObjectUtils.isEmpty(orderId)).collect(Collectors.toList()));
                orderIdList.add(0L);
            }

            if (!ObjectUtils.isEmpty(orderPageQuery.getStartDate()) && !ObjectUtils.isEmpty(orderPageQuery.getEndDate())) {
                list.add(criteriaBuilder.between(root.get("createTime").as(Date.class), orderPageQuery.getStartDate(), orderPageQuery.getEndDate()));
            }

            if (StringUtils.hasText(orderPageQuery.getOrderNo())) {
                list.add(criteriaBuilder.like(root.get("orderNo").as(String.class), SystemConstants.generateSqlLike(orderPageQuery.getOrderNo())));
            }

            if (StringUtils.hasText(orderPageQuery.getMdseName())) {
                List<MallOrderMdseDetails> orderMdseDetailsList = orderMdseDetailsRepository.findAllByMdseNameIsLike(SystemConstants.generateSqlLike(orderPageQuery.getMdseName()));
                orderIdList.addAll(orderMdseDetailsList.stream().map(MallOrderMdseDetails::getOrderId).filter(orderId -> !ObjectUtils.isEmpty(orderId)).collect(Collectors.toList()));
                orderIdList.add(0L);
            }

            if (!ObjectUtils.isEmpty(orderPageQuery.getMdseId())) {
                Result<List<MdsePurchaseRecordDTO>> purchaseRecordListResult = mdseFeignClient.getPurchaseRecordByMdseId(orderPageQuery.getMdseId());
                if (Result.isSuccess(purchaseRecordListResult)) {
                    orderIdList = Sets.newHashSet();
                    orderIdList.addAll(purchaseRecordListResult.getData().stream().map(MdsePurchaseRecordDTO::getOrderId).filter(Objects::nonNull).collect(Collectors.toList()));
                    orderIdList.add(0L);
                } else {
                    throw new BusinessException("????????????");
                }
            }

            if (StringUtils.hasText(orderPageQuery.getAddressee())) {
                List<MallOrderDeliveryDetails> orderDeliveryDetailsList = orderDeliveryDetailsRepository.findAllByAddresseeIsLike(SystemConstants.generateSqlLike(orderPageQuery.getAddressee()));
                orderIdList.addAll(orderDeliveryDetailsList.stream().map(MallOrderDeliveryDetails::getOrderId).filter(orderId -> !ObjectUtils.isEmpty(orderId)).collect(Collectors.toList()));
                orderIdList.add(0L);
            }

            if (StringUtils.hasText(orderPageQuery.getMobile())) {
                List<MallOrderDeliveryDetails> orderDeliveryDetailsList = orderDeliveryDetailsRepository.findAllByMobileLike(SystemConstants.generateSqlLike(orderPageQuery.getMobile()));
                orderIdList.addAll(orderDeliveryDetailsList.stream().map(MallOrderDeliveryDetails::getOrderId).filter(orderId -> !ObjectUtils.isEmpty(orderId)).collect(Collectors.toList()));
                orderIdList.add(0L);
            }

            if (Objects.nonNull(orderPageQuery.getType())) {
                Result<List<MdsePurchaseRecordDTO>> purchaseRecordListResult = mdseFeignClient.getPurchaseRecordByType(orderPageQuery.getType());

                if (Result.isSuccess(purchaseRecordListResult)) {
                    if (orderPageQuery.getType().equals(1)) {
                        orderIdList.retainAll(purchaseRecordListResult.getData().stream().map(MdsePurchaseRecordDTO::getOrderId).filter(Objects::nonNull).collect(Collectors.toList()));
                    }
                    if (orderPageQuery.getType().equals(2)) {
                        orderIdList = Sets.newHashSet();
                        orderIdList.addAll(purchaseRecordListResult.getData().stream().map(MdsePurchaseRecordDTO::getOrderId).filter(Objects::nonNull).collect(Collectors.toList()));
                        orderIdList.add(0L);
                    }

                } else {
                    throw new BusinessException("????????????");
                }
            }

            if (!ObjectUtils.isEmpty(orderIdList)) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("orderId").as(Long.class));
                orderIdList.forEach(in::value);
                list.add(in);
            }

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return orderRepository.findAll(specification, pageable);
    }

    /**
     * ???????????????????????????
     *
     * @param orderNo ?????????
     * @return ????????????
     */
    @Override
    public Optional<MallOrder> findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    /**
     * ??????????????????
     *
     * @param orderPaySuccess ????????????
     */
    @Override
    @GlobalTransactional(name = "mall-order-pay-success", rollbackFor = Exception.class)
    public void paySuccess(OrderPaySuccess orderPaySuccess) {

        Optional<MallOrder> orderOptional = findById(orderPaySuccess.getOrderId());
        MallOrder mallOrder = orderOptional.orElseThrow(() -> {

            log.error("???????????????????????????{}", orderPaySuccess.getOrderId());
            return new BusinessException("??????????????????????????????" + orderPaySuccess.getOrderId());
        });

        if (mallOrder.getOrderStatus() == OrderStatusEnum.UNPAID) {
            mallOrder.setOrderStatus(OrderStatusEnum.UNSHIPPED);
            if (mallOrder.getOrderDeliveryMethod() == OrderDeliveryMethodEnum.PICK_UP) {
                mallOrder.setOrderStatus(OrderStatusEnum.UNRECEIVED);
            }

            mallOrder.setOrderPayMethod(orderPaySuccess.getOrderPayMethod());
            mallOrder.setPayTime(orderPaySuccess.getPayTime());
            mallOrder.setAmountActuallyPaid(orderPaySuccess.getAmountActuallyPaid());
            mallOrder.setDeleted(SystemConstants.DELETED);
            mallOrder = orderRepository.save(mallOrder);

            boolean member = false;
            List<MallOrderMdseDetails> orderMdseDetailsList = orderMdseDetailsRepository.findAllByOrderId(mallOrder.getOrderId());
            CompletableFuture.runAsync(() -> orderMdseDetailsRepository.deleteAll(orderMdseDetailsList), executor);

            //????????????????????????????????????
            Map<Integer, List<MallOrderMdseDetails>> orderMdseDetailsGroup = orderMdseDetailsList.stream().filter(details -> Objects.nonNull(details.getType()) && Objects.nonNull(details.getCardId()) && details.getCardId().equals(0L)).collect(Collectors.groupingBy(MallOrderMdseDetails::getType));
            //????????????
            List<MallOrderMdseDetails> mdseDetailsList = orderMdseDetailsGroup.get(MdseConstants.MDSE_TYPE_MDSE);
            //?????????
            List<MallOrderMdseDetails> cardDetailsList = orderMdseDetailsGroup.get(MdseConstants.MDSE_TYPE_CARD);
            //??????????????????????????????
            Map<Long, List<MallOrderMdseDetails>> mdseDetailsGroup = Optional.ofNullable(mdseDetailsList).orElse(Lists.newArrayList()).stream().filter(mdseDetails -> Objects.nonNull(mdseDetails.getShopId())).collect(Collectors.groupingBy(MallOrderMdseDetails::getShopId));
            //????????????????????????????????????
            MallOrder createOrder = mallOrder;
            mdseDetailsGroup.forEach((shopId, mdseDetails) -> createShopMdseOrder(createOrder, mdseDetails));
            //???????????????
            if (!CollectionUtils.isEmpty(cardDetailsList)) {
                member = true;
                cardDetailsList.forEach(cardDetail -> createSingleCardOrder(orderMdseDetailsList, createOrder, cardDetail));
            }
            if (member) {
                CompletableFuture.runAsync(() -> userFeignClient.updateUser(UserDTO.builder().userId(createOrder.getUserId()).memberLevel(1).build()), executor);
            }
            //???????????????
            if (StringUtils.hasText(mallOrder.getOrderCouponIds())) {
                List<Long> couponIdList = Arrays.stream(mallOrder.getOrderCouponIds().split(",")).map(Long::valueOf).collect(Collectors.toList());
                couponIdList.forEach(couponId -> CompletableFuture.runAsync(() -> couponFeignClient.updateUserCoupon(UserCouponDTO.builder().userId(createOrder.getUserId()).couponId(couponId).build()), executor));
            }
        }


    }

    /**
     * ????????????????????????
     *
     * @param createOrder          ????????????
     * @param orderMdseDetailsList ????????????
     */
    private void createSingleCardOrder(List<MallOrderMdseDetails> orderMdseDetailsList, MallOrder createOrder, MallOrderMdseDetails cardDetail) {

        //????????????
        Integer quantity = cardDetail.getQuantity();
        cardDetail.setQuantity(1);
        calculatedAmount(Lists.newArrayList(cardDetail), createOrder);
        createOrder.setTotalAmount(createOrder.getTotalAmount().divide(new BigDecimal(String.valueOf(quantity)), 2, RoundingMode.HALF_UP));
        createOrder.setAmountPayable(createOrder.getAmountPayable().divide(new BigDecimal(String.valueOf(quantity)), 2, RoundingMode.HALF_UP));
        createOrder.setAmountActuallyPaid(createOrder.getAmountActuallyPaid().divide(new BigDecimal(String.valueOf(quantity)), 2, RoundingMode.HALF_UP));

        for (int i = 0; i < quantity; i++) {

            MallOrder cardOrder = createOrder(createOrder);

            List<MallOrderMdseDetails> cardDetails = orderMdseDetailsList.stream().filter(orderMdseDetail -> Objects.nonNull(orderMdseDetail.getCardId()) && cardDetail.getMdseId().equals(orderMdseDetail.getCardId())).collect(Collectors.toList());

            createOrderMdseDetails(cardOrder, 0L, cardDetail);
            cardDetails.forEach(cardMdseDetail -> createOrderMdseDetails(cardOrder, cardDetail.getMdseId(), cardMdseDetail));

            //?????????
            if (cardDetail.getInventoryReductionMethod() == InventoryReductionMethod.PAYMENT) {
                Long stockId = cardDetail.getStockId();
                CompletableFuture.runAsync(() -> mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(1).build()), executor);
            }
            //??????????????????
            CompletableFuture.runAsync(() -> mdseFeignClient.addMdsePurchaseRecord(MdsePurchaseRecordDTO.builder().orderId(cardOrder.getOrderId()).userId(cardOrder.getUserId()).mdseId(cardDetail.getMdseId()).type(cardDetail.getType()).build()), executor);
        }
    }

    private static void calculatedAmount(List<MallOrderMdseDetails> orderMdseDetailsList, MallOrder createOrder) {
        //????????????????????????
        BigDecimal totalAmount = createOrder.getTotalAmount();
        //???????????????????????????
        BigDecimal amountPayable = createOrder.getAmountPayable();
        //???????????????????????????
        BigDecimal amountActuallyPaid = createOrder.getAmountActuallyPaid();
        //?????????????????????
        BigDecimal shopTotalAmount = orderMdseDetailsList.stream()
                .filter(details -> Objects.nonNull(details.getType()) && Objects.nonNull(details.getCardId()) && details.getCardId().equals(0L))
                .map(mdseDetail -> mdseDetail.getMdsePrice().setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //????????????????????????
        BigDecimal multiple = shopTotalAmount.divide(totalAmount, 2, RoundingMode.HALF_UP);
        //????????????????????????
        BigDecimal shopAmountPayable = amountPayable.multiply(multiple).setScale(2, RoundingMode.HALF_UP);
        //????????????????????????
        BigDecimal shopAmountActuallyPaid = amountActuallyPaid.multiply(multiple).setScale(2, RoundingMode.HALF_UP);

        createOrder.setTotalAmount(shopTotalAmount);
        createOrder.setAmountPayable(shopAmountPayable);
        createOrder.setAmountActuallyPaid(shopAmountActuallyPaid);
    }

    private void createShopMdseOrder(MallOrder createOrder, List<MallOrderMdseDetails> mdseDetails) {

        //??????????????????
        calculatedAmount(mdseDetails, createOrder);
        MallOrder shopMdseOrder = createOrder(createOrder);
        mdseDetails.forEach(mdseDetail -> {
            MallOrderMdseDetails details = createOrderMdseDetails(shopMdseOrder, 0L, mdseDetail);
            //?????????
            if (mdseDetail.getInventoryReductionMethod() == InventoryReductionMethod.PAYMENT) {
                Long stockId = details.getStockId();
                Integer quantity = details.getQuantity();
                CompletableFuture.runAsync(() -> mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(quantity).build()), executor);
            }
            //??????????????????
            CompletableFuture.runAsync(() -> mdseFeignClient.addMdsePurchaseRecord(MdsePurchaseRecordDTO.builder().orderId(shopMdseOrder.getOrderId()).userId(shopMdseOrder.getUserId()).mdseId(mdseDetail.getMdseId()).type(mdseDetail.getType()).build()), executor);
        });
    }

    private MallOrder createOrder(MallOrder createOrder) {
        MallOrder cardOrder = new MallOrder();
        cardOrder.setOrderNo(createOrder.getOrderNo());
        cardOrder.setOrderStatus(createOrder.getOrderStatus());
        cardOrder.setUserId(createOrder.getUserId());
        cardOrder.setTotalAmount(createOrder.getTotalAmount());
        cardOrder.setAmountPayable(createOrder.getAmountPayable());
        cardOrder.setAmountActuallyPaid(createOrder.getAmountActuallyPaid());
        cardOrder.setOrderPayMethod(createOrder.getOrderPayMethod());
        cardOrder.setPayTime(createOrder.getPayTime());
        cardOrder.setRemarks(createOrder.getRemarks());
        cardOrder.setOrderDeliveryMethod(createOrder.getOrderDeliveryMethod());
        cardOrder.setOrderDeliveryDetailsId(createOrder.getOrderDeliveryDetailsId());
        cardOrder.setDeleted(SystemConstants.DELETED_NO);
        cardOrder.setOrderCouponIds(createOrder.getOrderCouponIds());
        cardOrder = orderRepository.save(cardOrder);
        return cardOrder;
    }

    private MallOrderMdseDetails createOrderMdseDetails(MallOrder cardOrder, Long cardId, MallOrderMdseDetails cardMdseDetail) {
        MallOrderMdseDetails details = new MallOrderMdseDetails();
        details.setOrderId(cardOrder.getOrderId());
        details.setCardId(cardId);
        details.setMdseId(cardMdseDetail.getMdseId());
        details.setNumber(cardMdseDetail.getNumber());
        details.setStockId(cardMdseDetail.getStockId());
        details.setShopId(cardMdseDetail.getShopId());
        details.setQuantity(cardMdseDetail.getQuantity());
        details.setType(cardMdseDetail.getType());
        details.setShopName(cardMdseDetail.getShopName());
        details.setMdseName(cardMdseDetail.getMdseName());
        details.setMdseStockSpec(cardMdseDetail.getMdseStockSpec());
        details.setMdsePicture(cardMdseDetail.getMdsePicture());
        details.setMdsePrice(cardMdseDetail.getMdsePrice());
        details.setUsageStatus(SystemConstants.STATUS_CLOSE);
        details.setUsageQuantity(0);
        details.setInventoryReductionMethod(cardMdseDetail.getInventoryReductionMethod());
        details.setDeleted(SystemConstants.DELETED_NO);
        return orderMdseDetailsRepository.save(details);
    }

    /**
     * ??????
     *
     * @param verifyOrderMdse ??????????????????
     */
    @Override
    @CacheEvict(value = "mall-order:order-info:", key = "'order-id:'+#verifyOrderMdse.orderId")
    public void verify(VerifyOrderMdse verifyOrderMdse) {
        List<MallOrderMdseDetails> mallOrderMdseDetails = orderMdseDetailsRepository.findAllByOrderId(verifyOrderMdse.getOrderId());

        Optional<MallOrderMdseDetails> mdseDetailsOptional = mallOrderMdseDetails.stream()
                .filter(orderMdse -> verifyOrderMdse.getMdseId().equals(orderMdse.getMdseId())
                        && verifyOrderMdse.getStockId().equals(orderMdse.getStockId())
                        && SystemConstants.STATUS_CLOSE.equals(orderMdse.getUsageStatus())).findFirst();

        if (!mdseDetailsOptional.isPresent()) {
            throw new BusinessException("????????????");
        }

        MallOrderMdseDetails orderMdseDetails = mdseDetailsOptional.get();

        Integer verifyQuantity = Objects.nonNull(verifyOrderMdse.getQuantity()) ? verifyOrderMdse.getQuantity() : orderMdseDetails.getQuantity();
        Integer usageQuantity = Objects.nonNull(orderMdseDetails.getUsageQuantity()) ? orderMdseDetails.getUsageQuantity() : 0;
        if ((verifyQuantity + usageQuantity) > orderMdseDetails.getQuantity()) {
            throw new BusinessException("?????????????????????");
        }

        orderMdseDetails.setUsageStatus(SystemConstants.STATUS_OPEN);

        if (verifyQuantity + usageQuantity < orderMdseDetails.getQuantity()) {
            orderMdseDetails.setUsageStatus(SystemConstants.STATUS_CLOSE);
        }
        orderMdseDetails.setUsageQuantity(verifyQuantity + usageQuantity);

        orderMdseDetails.setUsageDate(new Date());
        orderMdseDetailsRepository.save(orderMdseDetails);
    }

    /**
     * ????????????id??????????????????
     *
     * @param userId ??????id
     * @return ????????????
     */
    @Override
    public Map<String, Integer> findOrderNumberByUserId(Long userId) {

        //?????????
        Integer waitPay = orderRepository.countByUserIdAndOrderStatus(userId, OrderStatusEnum.UNPAID);
        //?????????
        Integer waitDelivery = orderRepository.countByUserIdAndOrderStatus(userId, OrderStatusEnum.UNSHIPPED);
        //?????????
        Integer waitReceive = orderRepository.countByUserIdAndOrderStatus(userId, OrderStatusEnum.UNRECEIVED);
        //?????????
        Integer waitEvaluate = orderRepository.countByUserIdAndOrderStatus(userId, OrderStatusEnum.UNEVALUATED);
        //??????/??????
        Integer refund = orderRepository.countByUserIdAndOrderStatus(userId, OrderStatusEnum.AFTER_SALES);

        Map<String, Integer> map = new HashMap<>(6);
        map.put("UNPAID", waitPay);
        map.put("UNSHIPPED", waitDelivery);
        map.put("UNRECEIVED", waitReceive);
        map.put("UNEVALUATED", waitEvaluate);
        map.put("AFTER_SALES", refund);
        return map;
    }

    /**
     * ????????????????????????
     *
     * @param dto                 ????????????
     * @param userId              ??????id
     * @param orderDeliveryMethod ????????????
     */
    @NotNull
    private MallOrder saveOrderBaseInfo(OrderDTO dto, Long userId, OrderDeliveryMethodEnum orderDeliveryMethod) {
        MallOrder mallOrder = new MallOrder();
        mallOrder.setOrderDeliveryMethod(orderDeliveryMethod);
        mallOrder.setOrderNo(IdUtil.getSnowflake(SystemConstants.WORK_ID, SystemConstants.MALL_ORDER_DATACENTER_ID).nextIdStr());
        mallOrder.setOrderStatus(OrderStatusEnum.UNPAID);
        mallOrder.setUserId(userId);
        mallOrder.setDeleted(SystemConstants.DELETED_NO);
        mallOrder.setRemarks(dto.getRemarks());
        mallOrder = orderRepository.save(mallOrder);
        return mallOrder;
    }

    /**
     * ??????????????????
     *
     * @param addressId           ????????????id
     * @param orderDeliveryMethod ????????????
     * @param mallOrder           ????????????
     * @return
     */
    private MallOrderDeliveryDetails saveOrderAddressInfo(Long addressId, OrderDeliveryMethodEnum orderDeliveryMethod, Long orderId) {

        Assert.notNull(addressId, "????????????????????????");
        Result<AddressInfo> addressInfoResult = addressFeignClient.getAddressById(addressId);
        if (!Result.isSuccess(addressInfoResult)) {
            throw new BusinessException("??????????????????");
        }
        AddressInfo addressInfo = addressInfoResult.getData();
        MallOrderDeliveryDetails orderDeliveryDetails = new MallOrderDeliveryDetails();
        orderDeliveryDetails.setOrderDeliveryMethod(orderDeliveryMethod);
        orderDeliveryDetails.setAddressId(addressId);
        orderDeliveryDetails.setAddressee(addressInfo.getName());
        orderDeliveryDetails.setMobile(addressInfo.getMobile());
        orderDeliveryDetails.setDetailedAddress(addressInfo.getRegion() + "" + addressInfo.getDetailedAddress());
        orderDeliveryDetails.setOrderId(orderId);
        orderDeliveryDetails.setDeleted(SystemConstants.DELETED_NO);
        return orderDeliveryDetailsRepository.save(orderDeliveryDetails);

    }

    /**
     * ??????????????????????????????
     *
     * @param orderMdseList ????????????id??????
     * @param orderId       ??????id
     * @return ????????????????????????
     */
    private List<MallOrderMdseDetails> saveOrderMdseDetailsList(List<OrderMdseDTO> orderMdseList, Long orderId) {
        List<MallOrderMdseDetails> orderMdseDetailsList = Lists.newArrayList();
        Result<MemberInfo> memberInfoResult = null;
        MdseInfoShow show = MdseInfoShow.builder().stock(SystemConstants.STATUS_OPEN).shop(SystemConstants.STATUS_OPEN).picture(SystemConstants.STATUS_OPEN).type(SystemConstants.STATUS_OPEN).build();
        for (OrderMdseDTO orderMdseDTO : orderMdseList) {

            String key = String.format("mall-order:order:mdse:%d:stock:%d", orderMdseDTO.getMdseId(), orderMdseDTO.getStockId());
            Boolean lock = redissonLockUtil.lock(key);
            if (Boolean.TRUE.equals(lock)) {

                Result<MdseInfo> mdseInfoResult = mdseFeignClient.getMdseOptionalById(orderMdseDTO.getMdseId(), show);

                if (!Result.isSuccess(mdseInfoResult)) {
                    redissonLockUtil.unlock(key);
                    throw new BusinessException("??????????????????");
                }

                MdseInfo mdseInfo = mdseInfoResult.getData();
                //????????????????????????
                if (Objects.equals(mdseInfo.getStatus(), SystemConstants.STATUS_CLOSE)) {
                    redissonLockUtil.unlock(key);
                    throw new BusinessException("???????????????");
                }

                if (mdseInfo.getClassify().equals(MdseConstants.MDSE_TYPE_CARD)) {
                    if (memberInfoResult == null) {
                        memberInfoResult = userFeignClient.getMemberByUserId(UserUtils.getUserId());
                    }

                    if (!Result.isSuccess(memberInfoResult)) {
                        throw new BusinessException("??????????????????????????????");
                    }

                }

                MallOrderMdseDetails mdseDetailsList = saveMdseDetails(orderId, 0L, mdseInfo, orderMdseDTO);
                orderMdseDetailsList.add(mdseDetailsList);

                redissonLockUtil.unlock(key);
            } else {
                throw new BusinessException("??????????????????");
            }

        }

        return orderMdseDetailsList;
    }

    /**
     * ????????????????????????
     *
     * @param orderId      ??????id
     * @param cardId       ??????????????????id
     * @param mdseInfo     ????????????
     * @param orderMdseDTO ??????id??????
     * @return ??????????????????
     */
    private MallOrderMdseDetails saveMdseDetails(Long orderId, Long cardId, MdseInfo mdseInfo, OrderMdseDTO orderMdseDTO) {

        MallOrderMdseDetails orderMdseDetails = new MallOrderMdseDetails();
        orderMdseDetails.setOrderId(orderId);
        orderMdseDetails.setMdseId(mdseInfo.getMdseId());
        orderMdseDetails.setMallId(mdseInfo.getMallId());
        orderMdseDetails.setUsageStatus(SystemConstants.STATUS_CLOSE);
        orderMdseDetails.setNumber(mdseInfo.getNumber());
        if (mdseInfo.getTypeInfo() != null) {
            orderMdseDetails.setMdseTypeId(mdseInfo.getTypeInfo().getTypeId());
        }
        StockInfo stockInfo = mdseInfo.getStockList()
                .stream()
                .filter(info -> info.getStockId().equals(orderMdseDTO.getStockId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("??????????????????"));
        if (stockInfo.getRemainingStock() < orderMdseDTO.getQuantity()) {
            throw new BusinessException("????????????");
        }

        //?????????????????????
        orderMdseDetails.setInventoryReductionMethod(mdseInfo.getInventoryReductionMethod());
        if (mdseInfo.getInventoryReductionMethod() == InventoryReductionMethod.CREATE_ORDER) {
            StockDTO stockDTO = new StockDTO();
            stockDTO.setStockId(stockInfo.getStockId());
            stockDTO.setTotalInventory(orderMdseDTO.getQuantity());
            mdseStockFeignClient.lessMdseStock(stockDTO);
            MallOrderMdseDetails finalOrderMdseDetails = orderMdseDetails;
            executor.execute(() -> mdseFeignClient.updateMdseSalesVolume(MdseDTO.builder().mdseId(finalOrderMdseDetails.getMdseId()).salesVolume(orderMdseDTO.getQuantity()).build()));
        }

        if (MdseConstants.MDSE_TYPE_MDSE.equals(mdseInfo.getClassify())) {
            ShopInfo shopInfo = Optional.of(mdseInfo.getShopInfo())
                    .orElseThrow(() -> new BusinessException("??????????????????"));
            orderMdseDetails.setShopId(shopInfo.getShopId());
            orderMdseDetails.setShopName(shopInfo.getName());
        }


        String pictureUrl = mdseInfo.getPictureList()
                .stream()
                .filter(info -> !ObjectUtils.isEmpty(info.getStockId()) && info.getStockId().equals(stockInfo.getStockId()))
                .findFirst()
                .orElse(ObjectUtils.isEmpty(mdseInfo.getPictureList()) ? PictureInfo.builder().url("").build() : mdseInfo.getPictureList().get(0)).getUrl();


        String stockSpec = Stream.of(stockInfo).map(stock -> {
            StringBuilder specStr = new StringBuilder();
            if (!ObjectUtils.isEmpty(stock.getSpecA())) {
                specStr.append(stock.getSpecA().getKey());
                specStr.append(":");
                specStr.append(stock.getSpecA().getValue());
            }
            if (!ObjectUtils.isEmpty(stock.getSpecB())) {
                specStr.append(" ");
                specStr.append(stock.getSpecB().getKey());
                specStr.append(":");
                specStr.append(stock.getSpecB().getValue());
            }
            if (!ObjectUtils.isEmpty(stock.getSpecC())) {
                specStr.append(" ");
                specStr.append(stock.getSpecC().getKey());
                specStr.append(":");
                specStr.append(stock.getSpecC().getValue());
            }
            return specStr.toString();
        }).findFirst().orElse("");

        orderMdseDetails.setStockId(stockInfo.getStockId());
        orderMdseDetails.setQuantity(orderMdseDTO.getQuantity());
        orderMdseDetails.setType(mdseInfo.getClassify());
        orderMdseDetails.setMdseName(mdseInfo.getName());
        orderMdseDetails.setMdseStockSpec(stockSpec);
        orderMdseDetails.setMdsePicture(pictureUrl);
        orderMdseDetails.setMdsePrice(stockInfo.getPrice());
        orderMdseDetails.setCardId(cardId);
        orderMdseDetails.setDeleted(SystemConstants.DELETED_NO);
        orderMdseDetails = orderMdseDetailsRepository.save(orderMdseDetails);
        if (mdseInfo.getClassify().equals(MdseConstants.MDSE_TYPE_CARD)) {
            List<MdseInfo> mdseInfoList = mdseInfo.getMdseInfoList();
            mdseInfoList.forEach(info -> saveMdseDetails(orderId, mdseInfo.getMdseId(), info, OrderMdseDTO.builder()
                    .mdseId(info.getMdseId())
                    .quantity(info.getStartingQuantity())
                    .shopId(Objects.nonNull(info.getShopInfo()) ? Objects.nonNull(info.getShopInfo().getShopId()) ? info.getShopInfo().getShopId() : 0L : 0L)
                    .stockId(info.getStockList().get(0).getStockId())
                    .build()));
        }

        return orderMdseDetails;
    }

    /**
     * ???????????????????????????
     *
     * @param couponIdList ?????????id??????
     * @return ???????????????
     */
    private List<UserCouponInfo> getUserCouponInfoList(List<Long> couponIdList) {

        if (couponIdList != null && !couponIdList.isEmpty()) {
            String couponIds = couponIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
            Result<List<UserCouponInfo>> userCouponResult = couponFeignClient.getUserCoupon(couponIds);
            if (Result.isSuccess(userCouponResult)) {
                List<UserCouponInfo> userCouponInfoList = userCouponResult.getData();
                if (!CollectionUtils.isEmpty(userCouponInfoList)) {
                    return userCouponInfoList;
                }
            } else {
                log.error("???????????????????????????:{}", userCouponResult);
                throw new BusinessException("???????????????????????????");
            }
        }
        return Lists.newArrayList();
    }

    /**
     * ?????????????????????
     *
     * @param userCouponInfoList ???????????????
     * @return ???????????????
     */
    private Map<CouponTypeEnum, UserCouponInfo> checkCoupon(List<UserCouponInfo> userCouponInfoList) {

        Map<CouponTypeEnum, UserCouponInfo> couponMap = new HashMap<>();

        if (!userCouponInfoList.isEmpty()) {
            for (UserCouponInfo userCouponInfo : userCouponInfoList) {
                //?????????????????????????????????
                if (userCouponInfo.getStatus() != CouponStateEnum.NORMAL) {
                    throw new BusinessException("??????????????????");
                }
                //???????????????????????????????????????
                if (userCouponInfo.getEffectiveTime().after(new Date()) || userCouponInfo.getInvalidTime().before(new Date())) {
                    throw new BusinessException("??????????????????????????????");
                }
                //??????????????????????????????????????????
                if (userCouponInfo.getType() == CouponTypeEnum.FULL_REDUCTION) {
                    if (couponMap.get(CouponTypeEnum.FULL_REDUCTION) != null) {
                        throw new BusinessException("???????????????????????????");
                    }
                    couponMap.put(CouponTypeEnum.FULL_REDUCTION, userCouponInfo);
                }
                if (userCouponInfo.getType() == CouponTypeEnum.DISCOUNT) {
                    if (couponMap.get(CouponTypeEnum.DISCOUNT) != null) {
                        throw new BusinessException("???????????????????????????");
                    }
                    couponMap.put(CouponTypeEnum.DISCOUNT, userCouponInfo);
                }
                if (userCouponInfo.getType() == CouponTypeEnum.EXCHANGE) {
                    if (couponMap.get(CouponTypeEnum.EXCHANGE) != null) {
                        throw new BusinessException("???????????????????????????");
                    }
                    couponMap.put(CouponTypeEnum.EXCHANGE, userCouponInfo);
                }
                if (userCouponInfo.getType() == CouponTypeEnum.CASH) {
                    if (couponMap.get(CouponTypeEnum.CASH) != null) {
                        throw new BusinessException("???????????????????????????");
                    }
                    couponMap.put(CouponTypeEnum.CASH, userCouponInfo);
                }
                if (userCouponInfo.getType() == CouponTypeEnum.FULL_GIFT) {
                    if (couponMap.get(CouponTypeEnum.FULL_GIFT) != null) {
                        throw new BusinessException("???????????????????????????");
                    }
                    couponMap.put(CouponTypeEnum.FULL_GIFT, userCouponInfo);
                }
                if (userCouponInfo.getType() == CouponTypeEnum.FREE_SHIPPING) {
                    if (couponMap.get(CouponTypeEnum.FREE_SHIPPING) != null) {
                        throw new BusinessException("???????????????????????????");
                    }
                    couponMap.put(CouponTypeEnum.FREE_SHIPPING, userCouponInfo);
                }
                if (userCouponInfo.getType() == CouponTypeEnum.OTHER) {
                    if (couponMap.get(CouponTypeEnum.OTHER) != null) {
                        throw new BusinessException("???????????????????????????????????????");
                    }
                    couponMap.put(CouponTypeEnum.OTHER, userCouponInfo);
                }

            }
        }
        return couponMap;
    }

    /**
     * ???????????????
     *
     * @param orderMdseDetailsList ??????????????????
     * @param payableAmount        ????????????
     * @param couponMap            ???????????????
     * @return ????????????
     */
    private BigDecimal checkFullReductionCoupon(List<MallOrderMdseDetails> orderMdseDetailsList, BigDecimal payableAmount, Map<CouponTypeEnum, UserCouponInfo> couponMap) {
        UserCouponInfo fullReductionCoupon = couponMap.get(CouponTypeEnum.FULL_REDUCTION);

        if (fullReductionCoupon != null) {
            //????????????????????????????????? 1-???????????? 2-???????????? 3-????????????????????? 4-???????????? 5-?????????????????????
            Integer productType = fullReductionCoupon.getProductType();
            BigDecimal mdseTotalAmount = BigDecimal.ZERO;

            //????????????
            if (productType == 1) {
                mdseTotalAmount = orderMdseDetailsList.stream()
                        .filter(mdseDetail -> mdseDetail.getMallId().equals(fullReductionCoupon.getMallId()))
                        .map(mdseDetail -> mdseDetail.getMdsePrice().multiply(new BigDecimal(String.valueOf(mdseDetail.getQuantity()))).setScale(2, RoundingMode.HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            //????????????
            if (productType == 2) {
                String productIds = fullReductionCoupon.getProductIds();
                List<Long> mdseIdList = Arrays.stream(productIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
                //??????????????????????????????????????????
                mdseTotalAmount = orderMdseDetailsList.stream()
                        .filter(mdseDetail -> mdseDetail.getMallId().equals(fullReductionCoupon.getMallId()))
                        .filter(mdseDetail -> mdseIdList.contains(mdseDetail.getMdseId()))
                        .map(mdseDetail -> mdseDetail.getMdsePrice().multiply(new BigDecimal(String.valueOf(mdseDetail.getQuantity()))).setScale(2, RoundingMode.HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            //?????????????????????
            if (productType == 3) {
                String productIds = fullReductionCoupon.getNotProductIds();
                List<Long> mdseIdList = Arrays.stream(productIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
                //?????????????????????????????????????????????
                mdseTotalAmount = orderMdseDetailsList.stream()
                        .filter(mdseDetail -> mdseDetail.getMallId().equals(fullReductionCoupon.getMallId()))
                        .filter(mdseDetail -> !mdseIdList.contains(mdseDetail.getMdseId()))
                        .map(mdseDetail -> mdseDetail.getMdsePrice().multiply(new BigDecimal(String.valueOf(mdseDetail.getQuantity()))).setScale(2, RoundingMode.HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            //????????????
            if (productType == 4) {
                String productCategoryIds = fullReductionCoupon.getProductCategoryIds();
                List<Long> mdseCategoryIdList = Arrays.stream(productCategoryIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
                //??????????????????????????????????????????
                mdseTotalAmount = orderMdseDetailsList.stream()
                        .filter(mdseDetail -> mdseDetail.getMallId().equals(fullReductionCoupon.getMallId()))
                        .filter(mdseDetail -> mdseCategoryIdList.contains(mdseDetail.getMdseTypeId()))
                        .map(mdseDetail -> mdseDetail.getMdsePrice().multiply(new BigDecimal(String.valueOf(mdseDetail.getQuantity()))).setScale(2, RoundingMode.HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            //?????????????????????
            if (productType == 5) {
                String productCategoryIds = fullReductionCoupon.getNotProductCategoryIds();
                List<Long> mdseCategoryIdList = Arrays.stream(productCategoryIds.split(",")).map(Long::valueOf).collect(Collectors.toList());
                //?????????????????????????????????????????????
                mdseTotalAmount = orderMdseDetailsList.stream()
                        .filter(mdseDetail -> mdseDetail.getMallId().equals(fullReductionCoupon.getMallId()))
                        .filter(mdseDetail -> !mdseCategoryIdList.contains(mdseDetail.getMdseTypeId()))
                        .map(mdseDetail -> mdseDetail.getMdsePrice().multiply(new BigDecimal(String.valueOf(mdseDetail.getQuantity()))).setScale(2, RoundingMode.HALF_UP))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            if (mdseTotalAmount.compareTo(fullReductionCoupon.getFullAmount()) < 0) {
                throw new BusinessException("??????????????????????????????");
            }
            //?????????????????????
            BigDecimal deductionAmount = fullReductionCoupon.getAmount();
            payableAmount = payableAmount.subtract(deductionAmount);
            //??????????????????????????????0
            if (payableAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("??????????????????????????????0");
            }
        }
        return payableAmount;
    }

    /**
     * ???????????????
     *
     * @param orderMdseDetailsList ??????????????????
     * @param payableAmount        ????????????
     * @param couponMap            ???????????????
     * @return ????????????
     */
    private BigDecimal checkDiscountCoupon(List<MallOrderMdseDetails> orderMdseDetailsList, BigDecimal payableAmount, Map<CouponTypeEnum, UserCouponInfo> couponMap) {
        // TODO: 2021/10/14 ???????????????
        return payableAmount.add(BigDecimal.ZERO);
    }

    /**
     * ???????????????
     *
     * @param orderMdseDetailsList ??????????????????
     * @param payableAmount        ????????????
     * @param couponMap            ???????????????
     * @return ????????????
     */
    private BigDecimal checkCashCoupon(List<MallOrderMdseDetails> orderMdseDetailsList, BigDecimal payableAmount, Map<CouponTypeEnum, UserCouponInfo> couponMap) {
        // TODO: 2021/10/14 ???????????????
        return payableAmount.add(BigDecimal.ZERO);
    }

    /**
     * ???????????????
     *
     * @param orderMdseDetailsList ??????????????????
     * @param payableAmount        ????????????
     * @param couponMap            ???????????????
     * @return ??????????????????
     */
    private List<MallOrderMdseDetails> checkExchangeCoupon(List<MallOrderMdseDetails> orderMdseDetailsList, BigDecimal payableAmount, Map<CouponTypeEnum, UserCouponInfo> couponMap) {
        // TODO: 2021/10/14 ???????????????
        return orderMdseDetailsList;
    }

    /**
     * ???????????????
     *
     * @param orderMdseDetailsList ??????????????????
     * @param payableAmount        ????????????
     * @param couponMap            ???????????????
     * @return ??????????????????
     */
    private List<MallOrderMdseDetails> checkFullPresentCoupon(List<MallOrderMdseDetails> orderMdseDetailsList, BigDecimal payableAmount, Map<CouponTypeEnum, UserCouponInfo> couponMap) {
        // TODO: 2021/10/14 ???????????????
        return orderMdseDetailsList;
    }

    /**
     * ???????????????
     *
     * @param orderMdseDetailsList ??????????????????
     * @param freightAmount        ????????????
     * @param couponMap            ???????????????
     * @return ????????????
     */
    private BigDecimal checkFreeShippingCoupon(List<MallOrderMdseDetails> orderMdseDetailsList, BigDecimal freightAmount, Map<CouponTypeEnum, UserCouponInfo> couponMap) {
        // TODO: 2021/10/14 ???????????????
        return freightAmount.add(BigDecimal.ZERO);
    }

}
