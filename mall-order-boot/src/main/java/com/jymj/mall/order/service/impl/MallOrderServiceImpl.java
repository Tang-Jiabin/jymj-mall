package com.jymj.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.redis.RedissonLockUtil;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.mdse.api.MdseFeignClient;
import com.jymj.mall.mdse.api.MdseStockFeignClient;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdseInfoShow;
import com.jymj.mall.mdse.dto.MdsePurchaseRecordDTO;
import com.jymj.mall.mdse.dto.StockDTO;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
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
import com.jymj.mall.user.vo.AddressInfo;
import com.jymj.mall.user.vo.MemberInfo;
import com.jymj.mall.user.vo.UserInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Sets;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 订单
 *
 * @author 唐嘉彬
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
    private final MallOrderRepository orderRepository;
    private final ThreadPoolTaskExecutor executor;
    private final MallOrderMdseDetailsRepository orderMdseDetailsRepository;
    private final MallOrderDeliveryDetailsRepository orderDeliveryDetailsRepository;

    /**
     * 创建订单
     *
     * @param dto 添加参数
     * @return 订单信息
     */
    @Override
    @GlobalTransactional(name = "mall-order-add", rollbackFor = Exception.class)
    public MallOrder add(OrderDTO dto) {

        Long userId = UserUtils.getUserId();
        Long addressId = dto.getAddressId();
        /*
         * TODO 优惠券和活动目前没有 预留字段
         *  List<Long> couponIdList = dto.getCouponIdList();
         *  List<Long> promotionIdList = dto.getPromotionIdList();
         */

        List<OrderMdseDTO> orderMdseList = dto.getOrderMdseList();
        OrderDeliveryMethodEnum orderDeliveryMethod = dto.getOrderDeliveryMethod();

        MallOrder mallOrder = new MallOrder();
        mallOrder.setOrderDeliveryMethod(orderDeliveryMethod);
        mallOrder.setOrderNo(IdUtil.getSnowflake(SystemConstants.WORK_ID, SystemConstants.MALL_ORDER_DATACENTER_ID).nextIdStr());
        mallOrder.setOrderStatus(OrderStatusEnum.UNPAID);
        mallOrder.setUserId(userId);
        mallOrder.setDeleted(SystemConstants.DELETED_NO);
        mallOrder.setRemarks(dto.getRemarks());
        mallOrder = orderRepository.save(mallOrder);

        if (orderDeliveryMethod == OrderDeliveryMethodEnum.EXPRESS) {
            if (ObjectUtils.isEmpty(addressId)) {
                throw new BusinessException("地址不能为空");
            }
            Result<AddressInfo> addressInfoResult = addressFeignClient.getAddressById(addressId);
            if (!Result.isSuccess(addressInfoResult)) {
                throw new BusinessException("地址信息错误");
            }
            AddressInfo addressInfo = addressInfoResult.getData();
            MallOrderDeliveryDetails orderDeliveryDetails = new MallOrderDeliveryDetails();
            orderDeliveryDetails.setOrderDeliveryMethod(OrderDeliveryMethodEnum.EXPRESS);
            orderDeliveryDetails.setAddressId(addressId);
            orderDeliveryDetails.setAddressee(addressInfo.getName());
            orderDeliveryDetails.setMobile(addressInfo.getMobile());
            orderDeliveryDetails.setDetailedAddress(addressInfo.getRegion() + "" + addressInfo.getDetailedAddress());
            orderDeliveryDetails.setOrderId(mallOrder.getOrderId());
            orderDeliveryDetails.setDeleted(SystemConstants.DELETED_NO);
            orderDeliveryDetails = orderDeliveryDetailsRepository.save(orderDeliveryDetails);
            mallOrder.setOrderDeliveryDetailsId(orderDeliveryDetails.getOrderDeliveryDetailsId());
        }

        List<MallOrderMdseDetails> orderMdseDetailsList = saveOrderMdseDetailsList(orderMdseList, mallOrder.getOrderId());

        BigDecimal totalAmount = orderMdseDetailsList.stream()
                .map(MallOrderMdseDetails::getMdsePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        mallOrder.setTotalAmount(totalAmount);
        mallOrder.setAmountPayable(totalAmount);


        return orderRepository.save(mallOrder);
    }

    /**
     * 保存订单商品详情集合
     *
     * @param orderMdseList 订单商品id集合
     * @param orderId       订单id
     * @return 订单商品详情集合
     */
    private List<MallOrderMdseDetails> saveOrderMdseDetailsList(List<OrderMdseDTO> orderMdseList, Long orderId) {
        List<MallOrderMdseDetails> orderMdseDetailsList = Lists.newArrayList();
        Result<MemberInfo> memberInfoResult = null;
        MdseInfoShow show = MdseInfoShow.builder().stock(1).shop(1).picture(1).build();
        for (OrderMdseDTO orderMdseDTO : orderMdseList) {
            Integer type = orderMdseDTO.getType();

            String key = String.format("mall-order:order:mdse:%d:stock:%d", orderMdseDTO.getMdseId(), orderMdseDTO.getStockId());
            Boolean lock = redissonLockUtil.lock(key);
            if (Boolean.TRUE.equals(lock)) {

                if (type.equals(2)) {
                    if (memberInfoResult == null) {
                        memberInfoResult = userFeignClient.getMemberByUserId(UserUtils.getUserId());
                    }

                    if (!Result.isSuccess(memberInfoResult)) {
                        throw new BusinessException("请先填写会员基本信息");
                    }

                }

                Result<MdseInfo> mdseInfoResult = mdseFeignClient.getMdseOptionalById(orderMdseDTO.getMdseId(), show);

                if (!Result.isSuccess(mdseInfoResult)) {
                    redissonLockUtil.unlock(key);
                    throw new BusinessException("商品信息错误");
                }

                MdseInfo mdseInfo = mdseInfoResult.getData();
                MallOrderMdseDetails mdseDetailsList = saveMdseDetails(orderId, 0L, mdseInfo, orderMdseDTO);
                orderMdseDetailsList.add(mdseDetailsList);

                redissonLockUtil.unlock(key);
            } else {
                throw new BusinessException("创建订单失败");
            }

        }

        return orderMdseDetailsList;
    }

    /**
     * 保存订单商品详情
     *
     * @param orderId            订单id
     * @param orderMdseDetailsId
     * @param mdseInfo           商品信息
     * @param orderMdseDTO       商品id信息
     * @return 订单商品详情
     */
    private MallOrderMdseDetails saveMdseDetails(Long orderId, Long orderMdseDetailsId, MdseInfo mdseInfo, OrderMdseDTO orderMdseDTO) {

        MallOrderMdseDetails orderMdseDetails = new MallOrderMdseDetails();
        orderMdseDetails.setOrderId(orderId);
        orderMdseDetails.setMdseId(mdseInfo.getMdseId());
        orderMdseDetails.setUsageStatus(SystemConstants.STATUS_CLOSE);
        StockInfo stockInfo = mdseInfo.getStockList()
                .stream()
                .filter(info -> info.getStockId().equals(orderMdseDTO.getStockId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("库存信息错误"));
        if (stockInfo.getRemainingStock() < orderMdseDTO.getQuantity()) {
            throw new BusinessException("库存不足");
        }

        //创建订单减库存
        orderMdseDetails.setInventoryReductionMethod(mdseInfo.getInventoryReductionMethod());
        if (mdseInfo.getInventoryReductionMethod() == InventoryReductionMethod.CREATE_ORDER) {
            StockDTO stockDTO = new StockDTO();
            stockDTO.setStockId(stockInfo.getStockId());
            stockDTO.setTotalInventory(orderMdseDTO.getQuantity());
            mdseStockFeignClient.lessMdseStock(stockDTO);

            mdseFeignClient.updateMdseSalesVolume(MdseDTO.builder().mdseId(orderMdseDetails.getMdseId()).salesVolume(orderMdseDTO.getQuantity()).build());
        }

        if (mdseInfo.getClassify() == 1) {
            ShopInfo shopInfo = mdseInfo.getShopInfoList()
                    .stream()
                    .filter(info -> info.getShopId().equals(orderMdseDTO.getShopId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("店铺信息错误"));
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

        BigDecimal price = stockInfo.getPrice();
        BigDecimal mdsePrice = price.multiply(new BigDecimal(String.valueOf(orderMdseDTO.getQuantity())));
        orderMdseDetails.setStockId(stockInfo.getStockId());
        orderMdseDetails.setQuantity(orderMdseDTO.getQuantity());
        orderMdseDetails.setType(mdseInfo.getClassify());
        orderMdseDetails.setMdseName(mdseInfo.getName());
        orderMdseDetails.setMdseStockSpec(stockSpec);
        orderMdseDetails.setMdsePicture(pictureUrl);
        orderMdseDetails.setMdsePrice(mdsePrice);
        orderMdseDetails.setCardId(orderMdseDetailsId);
        orderMdseDetails.setDeleted(SystemConstants.DELETED_NO);
        orderMdseDetails = orderMdseDetailsRepository.save(orderMdseDetails);
        if (mdseInfo.getClassify() == 2) {
            List<MdseInfo> mdseInfoList = mdseInfo.getMdseInfoList();
            for (MdseInfo info : mdseInfoList) {
                saveMdseDetails(orderId, orderMdseDetails.getOrderMdseDetailsId(), info, OrderMdseDTO.builder()
                        .mdseId(info.getMdseId())
                        .quantity(1)
                        .shopId(info.getShopInfoList().get(0).getShopId())
                        .stockId(info.getStockList().get(0).getStockId())
                        .build());
            }
        }

        return orderMdseDetails;
    }


    /**
     * 修改订单
     *
     * @param dto 修改dto
     * @return 订单信息
     */
    @Override
    @GlobalTransactional(name = "mall-order-update", rollbackFor = Exception.class)
    @CacheEvict(value = "mall-order:order-info:", key = "'order-id:'+#dto.orderId")
    public Optional<MallOrder> update(OrderDTO dto) {
        if (!ObjectUtils.isEmpty(dto)) {
            if (ObjectUtils.isEmpty(dto.getOrderId()) && ObjectUtils.isEmpty(dto.getOrderNo())) {
                throw new BusinessException("订单不存在");
            }

            Optional<MallOrder> mallOrderOptional = ObjectUtils.isEmpty(dto.getOrderId()) ? findByOrderNo(dto.getOrderNo()) : findById(dto.getOrderId());

            MallOrder mallOrder = mallOrderOptional.orElseThrow(() -> new BusinessException("订单不存在"));
            boolean update = false;
            if (!ObjectUtils.isEmpty(dto.getStatusEnum())) {
                updateOrderStatus(dto.getStatusEnum(), mallOrder);
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
     * 更新订单地址
     *
     * @param addressId 用户地址id
     * @param mallOrder 订单信息
     */
    private void updateOrderAddress(Long addressId, MallOrder mallOrder) {
        Result<AddressInfo> addressInfoResult = addressFeignClient.getAddressById(addressId);
        if (!Result.isSuccess(addressInfoResult)) {
            throw new BusinessException("地址不存在");
        }
        AddressInfo addressInfo = addressInfoResult.getData();
        Optional<MallOrderDeliveryDetails> deliveryDetailsOptional = orderDeliveryDetailsRepository.findById(mallOrder.getOrderDeliveryDetailsId());
        MallOrderDeliveryDetails orderDeliveryDetails = deliveryDetailsOptional.orElseThrow(() -> new BusinessException("地址信息错误"));
        orderDeliveryDetails.setAddressee(addressInfo.getName());
        orderDeliveryDetails.setMobile(addressInfo.getMobile());
        orderDeliveryDetails.setAddressId(addressId);
        orderDeliveryDetails.setDetailedAddress(addressInfo.getRegion() + " " + addressInfo.getDetailedAddress());
        orderDeliveryDetails.setOrderDeliveryMethod(OrderDeliveryMethodEnum.EXPRESS);
        orderDeliveryDetailsRepository.save(orderDeliveryDetails);
    }

    /**
     * 更新订单状态
     *
     * @param statusEnum 订单状态
     * @param mallOrder  订单信息
     */
    private void updateOrderStatus(OrderStatusEnum statusEnum, MallOrder mallOrder) {
        switch (statusEnum) {
            case UNPAID:
                //未付款
                break;
            case UNSHIPPED:
                //未发货
                break;
            case UNRECEIVED:
                //未收货
                break;
            case COMPLETED:
                //已完成
                break;
            case CLOSED:
                //关闭
            case CANCELED:
                //取消
                updateOrderStatusClosedOrCanceled(statusEnum, mallOrder);
                break;
            case AFTER_SALES:
                //售后
                break;
        }


        mallOrder.setOrderStatus(statusEnum);
    }

    /**
     * 更新订单状态到关闭或取消（恢复库存用）
     *
     * @param statusEnum 订单状态
     * @param mallOrder  订单信息
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
                if (mallOrder.getOrderStatus() == OrderStatusEnum.UNSHIPPED && orderMdseDetails.getType() == 1) {
                    Long stockId = orderMdseDetails.getStockId();
                    Integer quantity = orderMdseDetails.getQuantity();
                    mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(-quantity).build());
                    mdseFeignClient.updateMdseSalesVolume(MdseDTO.builder().mdseId(orderMdseDetails.getMdseId()).salesVolume(-quantity).build());
                    //退款
                    executor.execute(() -> {

                    });
                }
            }
        }
    }

    /**
     * 删除订单
     *
     * @param ids id集合 字符串类型 英文,分割
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
     * 根据id查询订单
     *
     * @param id id
     * @return 订单信息
     */
    @Override
    public Optional<MallOrder> findById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * 实体转VO
     *
     * @param entity 实体
     * @return 订单vo信息
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
            //订单商品详情
            List<MallOrderMdseDetails> orderMdseDetailsList = orderMdseDetailsRepository.findAllByOrderId(entity.getOrderId());
            if (!CollectionUtils.isEmpty(orderMdseDetailsList)) {
                List<MallOrderMdseDetailsInfo> orderMdseDetailsInfoList = orderMdseDetailsList2vo(orderMdseDetailsList, Boolean.FALSE);
                orderInfo.setOrderMdseDetailsInfoList(orderMdseDetailsInfoList);
            }

            //订单收货地址信息
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
            //订单用户信息
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
     * 订单商品实体集合转VO
     *
     * @param orderMdseDetailsList 订单商品实体集合
     * @return 视图对象集合
     */
    private List<MallOrderMdseDetailsInfo> orderMdseDetailsList2vo(List<MallOrderMdseDetails> orderMdseDetailsList, boolean carded) {

        List<CompletableFuture<MallOrderMdseDetailsInfo>> futureList = Optional.of(orderMdseDetailsList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(Objects::nonNull)
                .filter(entity -> Boolean.FALSE.equals(carded) ? entity.getCardId() == 0 : Boolean.TRUE)
                .map(entity -> CompletableFuture.supplyAsync(() -> orderCardDetails2vo(entity, orderMdseDetailsList), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


    }

    private MallOrderMdseDetailsInfo orderCardDetails2vo(MallOrderMdseDetails orderMdseDetails, List<MallOrderMdseDetails> orderMdseDetailsList) {
        MallOrderMdseDetailsInfo info = orderMdseDetails2vo(orderMdseDetails);
        info.setCardMdseInfoList(orderMdseDetailsList2vo(
                orderMdseDetailsList
                        .stream()
                        .filter(details -> details.getCardId().equals(orderMdseDetails.getOrderMdseDetailsId()))
                        .collect(Collectors.toList()), Boolean.TRUE));
        return info;
    }

    /**
     * 订单商品实体转VO
     *
     * @param mallOrderMdseDetails 订单商品实体
     * @return 视图对象
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
            info.setMdseStockSpec(mallOrderMdseDetails.getMdseStockSpec());
            info.setUsageStatus(mallOrderMdseDetails.getUsageStatus());
            info.setMdsePicture(mallOrderMdseDetails.getMdsePicture());
            info.setMdsePrice(mallOrderMdseDetails.getMdsePrice());
        }

        return info;
    }

    /**
     * 订单集合转VO
     *
     * @param entityList 实体列表
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
     * 订单分页
     *
     * @param orderPageQuery 分页参数
     * @return 分页数据
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
               if (Result.isSuccess(purchaseRecordListResult)){
                   orderIdList = Sets.newHashSet();
                   orderIdList.addAll(purchaseRecordListResult.getData().stream().map(MdsePurchaseRecordDTO::getOrderId).filter(Objects::nonNull).collect(Collectors.toList()));
                   orderIdList.add(0L);
               }else {
                   throw new BusinessException("服务异常");
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
                    throw new BusinessException("服务异常");
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
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    @Override
    public Optional<MallOrder> findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    /**
     * 支付成功回调
     *
     * @param orderPaySuccess 支付信息
     */
    @Override
    @GlobalTransactional(name = "mall-order-pay-success", rollbackFor = Exception.class)
    public void paySuccess(OrderPaySuccess orderPaySuccess) {
        String key = String.format("mall-order:order:pay-success:%d", orderPaySuccess.getOrderId());
        Boolean lock = redissonLockUtil.lock(key);
        if (Boolean.TRUE.equals(lock)) {
            Optional<MallOrder> orderOptional = findById(orderPaySuccess.getOrderId());
            MallOrder mallOrder = orderOptional.orElseThrow(() -> {
                redissonLockUtil.unlock(key);
                return new BusinessException("支付成功订单不存在");
            });
            if (mallOrder.getOrderStatus() == OrderStatusEnum.UNPAID) {
                mallOrder.setOrderStatus(OrderStatusEnum.UNSHIPPED);
                if (mallOrder.getOrderDeliveryMethod() == OrderDeliveryMethodEnum.PICK_UP) {
                    mallOrder.setOrderStatus(OrderStatusEnum.COMPLETED);
                }

                mallOrder.setOrderPayMethod(orderPaySuccess.getOrderPayMethod());
                mallOrder.setPayTime(orderPaySuccess.getPayTime());
                mallOrder.setAmountActuallyPaid(orderPaySuccess.getAmountActuallyPaid());
                mallOrder = orderRepository.save(mallOrder);
                Long userId = mallOrder.getUserId();

                //付款减库存
                List<MallOrderMdseDetails> orderMdseDetailsList = orderMdseDetailsRepository.findAllByOrderId(mallOrder.getOrderId());
                for (MallOrderMdseDetails orderMdseDetails : orderMdseDetailsList) {
                    if (orderMdseDetails.getInventoryReductionMethod() == InventoryReductionMethod.PAYMENT && orderMdseDetails.getType() == 1) {
                        Long stockId = orderMdseDetails.getStockId();
                        Integer quantity = orderMdseDetails.getQuantity();
                        mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(quantity).build());
                    }
                    MallOrder finalMallOrder = mallOrder;
                    executor.execute(() -> mdseFeignClient.addMdsePurchaseRecord(MdsePurchaseRecordDTO.builder().orderId(finalMallOrder.getOrderId()).userId(userId).mdseId(orderMdseDetails.getMdseId()).type(orderMdseDetails.getType()).build()));
                }
            }
            redissonLockUtil.unlock(key);
        }

    }

    @Override
    @CacheEvict(value = "mall-order:order-info:", key = "'order-id:'+#verifyOrderMdse.orderId")
    public void verify(VerifyOrderMdse verifyOrderMdse) {
        List<MallOrderMdseDetails> mallOrderMdseDetails = orderMdseDetailsRepository.findAllByOrderId(verifyOrderMdse.getOrderId());

        Optional<MallOrderMdseDetails> mdseDetailsOptional = mallOrderMdseDetails.stream()
                .filter(orderMdse -> verifyOrderMdse.getMdseId().equals(orderMdse.getMdseId())
                        && verifyOrderMdse.getStockId().equals(orderMdse.getStockId())
                        && SystemConstants.STATUS_CLOSE.equals(orderMdse.getUsageStatus())).findFirst();

        if (!mdseDetailsOptional.isPresent()) {
            throw new BusinessException("核销失败");
        }

        MallOrderMdseDetails orderMdseDetails = mdseDetailsOptional.get();
        orderMdseDetails.setUsageStatus(SystemConstants.STATUS_OPEN);
        orderMdseDetailsRepository.save(orderMdseDetails);
    }

}
