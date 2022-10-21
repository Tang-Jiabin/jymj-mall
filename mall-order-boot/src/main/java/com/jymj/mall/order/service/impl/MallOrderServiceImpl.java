package com.jymj.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.mdse.api.MdseFeignClient;
import com.jymj.mall.mdse.api.MdseStockFeignClient;
import com.jymj.mall.mdse.dto.MdseInfoShow;
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
import com.jymj.mall.shop.vo.ShopInfo;
import com.jymj.mall.user.api.UserAddressFeignClient;
import com.jymj.mall.user.vo.AddressInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    //    private final RedissonClient redissonClient;
    private final MdseStockFeignClient mdseStockFeignClient;
    private final UserAddressFeignClient addressFeignClient;
    private final MallOrderRepository orderRepository;
    private final MallOrderMdseDetailsRepository orderMdseDetailsRepository;
    private final MallOrderDeliveryDetailsRepository orderDeliveryDetailsRepository;

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
            orderDeliveryDetails.setDeleted(SystemConstants.DELETED_NO);
            orderDeliveryDetails = orderDeliveryDetailsRepository.save(orderDeliveryDetails);
            mallOrder.setOrderDeliveryDetailsId(orderDeliveryDetails.getOrderDeliveryDetailsId());
        }
        mallOrder.setOrderDeliveryMethod(orderDeliveryMethod);
        mallOrder.setOrderNo(IdUtil.getSnowflake(SystemConstants.WORK_ID, SystemConstants.MALL_ORDER_DATACENTER_ID).nextIdStr());
        mallOrder.setOrderStatus(OrderStatusEnum.UNPAID);
        mallOrder.setUserId(userId);
        mallOrder.setDeleted(SystemConstants.DELETED_NO);
        mallOrder.setRemarks(dto.getRemarks());
        mallOrder = orderRepository.save(mallOrder);


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

        MdseInfoShow show = MdseInfoShow.builder().stock(true).shop(true).picture(true).build();
        for (OrderMdseDTO orderMdseDTO : orderMdseList) {
            Integer type = orderMdseDTO.getType();
            if (type == 1) {
                String key = String.format("lock:order:mdse:%d:stock:%d", orderMdseDTO.getMdseId(), orderMdseDTO.getStockId());
//                RLock lock = redissonClient.getLock(key);

                try {
//                    boolean tryLock = lock.tryLock(5, TimeUnit.SECONDS);
//                    if (!tryLock) {
//                        throw new BusinessException("请稍后再试");
//                    }
                    Result<MdseInfo> mdseInfoResult = mdseFeignClient.getMdseOptionalById(orderMdseDTO.getMdseId(), show);
                    if (!Result.isSuccess(mdseInfoResult)) {
                        throw new BusinessException("商品信息错误");
                    }
                    MdseInfo mdseInfo = mdseInfoResult.getData();
                    MallOrderMdseDetails mdseDetailsList = saveMdseDetails(orderId, mdseInfo, orderMdseDTO);
                    orderMdseDetailsList.add(mdseDetailsList);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BusinessException("请稍后再试");
                } finally {
//                    lock.unlock();
                }


            }
        }

        return orderMdseDetailsList;
    }

    /**
     * 保存订单商品详情
     *
     * @param orderId      订单id
     * @param mdseInfo     商品信息
     * @param orderMdseDTO 商品id信息
     * @return 订单商品详情
     */
    private MallOrderMdseDetails saveMdseDetails(Long orderId, MdseInfo mdseInfo, OrderMdseDTO orderMdseDTO) {

        MallOrderMdseDetails orderMdseDetails = new MallOrderMdseDetails();
        orderMdseDetails.setOrderId(orderId);
        orderMdseDetails.setMdseId(mdseInfo.getMdseId());

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
        }

        ShopInfo shopInfo = mdseInfo.getShopInfoList()
                .stream()
                .filter(info -> info.getShopId().equals(orderMdseDTO.getShopId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("店铺信息错误"));

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
        orderMdseDetails.setShopId(shopInfo.getShopId());
        orderMdseDetails.setQuantity(orderMdseDTO.getQuantity());
        orderMdseDetails.setType(orderMdseDTO.getType());
        orderMdseDetails.setShopName(shopInfo.getName());
        orderMdseDetails.setMdseName(mdseInfo.getName());
        orderMdseDetails.setMdseStockSpec(stockSpec);
        orderMdseDetails.setMdsePicture(pictureUrl);
        orderMdseDetails.setMdsePrice(mdsePrice);
        orderMdseDetails.setDeleted(SystemConstants.DELETED_NO);

        return orderMdseDetailsRepository.save(orderMdseDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

    private void updateOrderStatusClosedOrCanceled(OrderStatusEnum statusEnum, MallOrder mallOrder) {
        if (statusEnum == OrderStatusEnum.CANCELED || statusEnum == OrderStatusEnum.CLOSED) {
            List<MallOrderMdseDetails> mdseDetailsList = orderMdseDetailsRepository.findAllByOrderId(mallOrder.getOrderId());
            for (MallOrderMdseDetails orderMdseDetails : mdseDetailsList) {
                if (mallOrder.getOrderStatus() == OrderStatusEnum.UNPAID && orderMdseDetails.getInventoryReductionMethod() == InventoryReductionMethod.CREATE_ORDER && orderMdseDetails.getType() == 1) {
                    Long stockId = orderMdseDetails.getStockId();
                    Integer quantity = orderMdseDetails.getQuantity();
                    mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(-quantity).build());
                }
                if (mallOrder.getOrderStatus() == OrderStatusEnum.UNSHIPPED && orderMdseDetails.getType() == 1) {
                    Long stockId = orderMdseDetails.getStockId();
                    Integer quantity = orderMdseDetails.getQuantity();
                    mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(-quantity).build());
                }
            }
        }
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MallOrder> orderList = orderRepository.findAllById(idList);
            orderList = orderList.stream().filter(order -> order.getUserId().equals(UserUtils.getUserId())).collect(Collectors.toList());
            orderRepository.deleteAll(orderList);
        }
    }

    @Override
    public Optional<MallOrder> findById(Long id) {
        return orderRepository.findById(id);
    }

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
            List<MallOrderMdseDetails> orderMdseDetailsList = orderMdseDetailsRepository.findAllByOrderId(entity.getOrderId());
            List<MallOrderMdseDetailsInfo> orderMdseDetailsInfoList = orderMdseDetailsList2vo(orderMdseDetailsList);
            orderInfo.setOrderMdseDetailsInfoList(orderMdseDetailsInfoList);
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

            return orderInfo;
        }
        return null;
    }

    private List<MallOrderMdseDetailsInfo> orderMdseDetailsList2vo(List<MallOrderMdseDetails> orderMdseDetailsList) {

        return Optional.of(orderMdseDetailsList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::orderMdseDetails2vo)
                .collect(Collectors.toList());
    }

    private MallOrderMdseDetailsInfo orderMdseDetails2vo(MallOrderMdseDetails mallOrderMdseDetails) {
        MallOrderMdseDetailsInfo info = new MallOrderMdseDetailsInfo();
        info.setMdseId(mallOrderMdseDetails.getMdseId());
        info.setStockId(mallOrderMdseDetails.getStockId());
        info.setShopId(mallOrderMdseDetails.getShopId());
        info.setQuantity(mallOrderMdseDetails.getQuantity());
        info.setType(mallOrderMdseDetails.getType());
        info.setShopName(mallOrderMdseDetails.getShopName());
        info.setMdseName(mallOrderMdseDetails.getMdseName());
        info.setMdseStockSpec(mallOrderMdseDetails.getMdseStockSpec());
        info.setMdsePicture(mallOrderMdseDetails.getMdsePicture());
        info.setMdsePrice(mallOrderMdseDetails.getMdsePrice());
        return info;
    }

    @Override
    public List<MallOrderInfo> list2vo(List<MallOrder> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MallOrder> findPage(OrderPageQuery orderPageQuery) {
        Pageable pageable = PageUtils.getPageable(orderPageQuery);
        Specification<MallOrder> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (!ObjectUtils.isEmpty(orderPageQuery.getUserId())) {
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), orderPageQuery.getUserId()));
            }

            if (!ObjectUtils.isEmpty(orderPageQuery.getOrderStatus())) {
                list.add(criteriaBuilder.equal(root.get("orderStatus").as(OrderStatusEnum.class), orderPageQuery.getOrderStatus()));
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return orderRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<MallOrder> findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    @Override
    @GlobalTransactional(name = "mall-order-pay-success", rollbackFor = Exception.class)
    public void paySuccess(OrderPaySuccess orderPaySuccess) {
        Optional<MallOrder> orderOptional = findById(orderPaySuccess.getOrderId());
        MallOrder mallOrder = orderOptional.orElseThrow(() -> new BusinessException("支付成功订单不存在"));

        mallOrder.setOrderStatus(OrderStatusEnum.UNSHIPPED);
        if (mallOrder.getOrderDeliveryMethod() == OrderDeliveryMethodEnum.PICK_UP) {
            mallOrder.setOrderStatus(OrderStatusEnum.COMPLETED);
        }

        mallOrder.setOrderPayMethod(orderPaySuccess.getOrderPayMethod());
        mallOrder.setPayTime(orderPaySuccess.getPayTime());
        mallOrder.setAmountActuallyPaid(orderPaySuccess.getAmountActuallyPaid());
        orderRepository.save(mallOrder);

        //付款减库存
        List<MallOrderMdseDetails> orderMdseDetailsList = orderMdseDetailsRepository.findAllByOrderId(mallOrder.getOrderId());
        for (MallOrderMdseDetails orderMdseDetails : orderMdseDetailsList) {
            if (orderMdseDetails.getInventoryReductionMethod() == InventoryReductionMethod.PAYMENT && orderMdseDetails.getType() == 1) {
                Long stockId = orderMdseDetails.getStockId();
                Integer quantity = orderMdseDetails.getQuantity();
                mdseStockFeignClient.lessMdseStock(StockDTO.builder().stockId(stockId).totalInventory(-quantity).build());
            }
        }
    }

}
