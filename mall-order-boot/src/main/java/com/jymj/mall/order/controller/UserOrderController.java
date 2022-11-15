package com.jymj.mall.order.controller;

import com.jymj.mall.common.enums.EnumTypeInfo;
import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import com.jymj.mall.common.enums.OrderPayMethodEnum;
import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.order.config.MQConfig;
import com.jymj.mall.order.dto.OrderDTO;
import com.jymj.mall.order.dto.OrderPageQuery;
import com.jymj.mall.order.entity.MallOrder;
import com.jymj.mall.order.service.OrderService;
import com.jymj.mall.order.vo.MallOrderInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 用户订单
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-12
 */
@Api(tags = "用户订单")
@RestController
@RequestMapping("/api/v1/user/order")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;
    private final ThreadPoolTaskExecutor executor;

    @ApiOperation(value = "创建订单")
    @PostMapping
    public Result<MallOrderInfo> addOrder(@Valid @RequestBody OrderDTO orderDTO) {
        MallOrder mallOrder = orderService.add(orderDTO);
        MallOrderInfo mallOrderInfo = orderService.entity2vo(mallOrder);
        executor.execute(() -> rabbitTemplate.convertAndSend(MQConfig.EXCHANGE_DELAY, MQConfig.ROUTING_KEY_QUEUE_ORDER, mallOrderInfo));
        return Result.success(mallOrderInfo);
    }


    @ApiOperation(value = "删除订单")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteDept(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        orderService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "更新订单")
    @PutMapping
    public Result<Object> update(@RequestBody OrderDTO orderDTO) {
        orderService.update(orderDTO);
        return Result.success();
    }

    @ApiOperation(value = "订单信息")
    @GetMapping("/id/{orderId}/info")
    @Cacheable(cacheNames = "mall-order:order-info:", key = "'order-id:'+#orderId")
    public Result<MallOrderInfo> getOrderById(@PathVariable Long orderId) {
        Optional<MallOrder> orderOptional = orderService.findById(orderId);

        return orderOptional
                .filter(orderInfo -> orderInfo.getUserId().equals(UserUtils.getUserId()))
                .map(orderInfo -> Result.success(orderService.entity2vo(orderInfo)))
                .orElse(Result.failed("订单不存在"));

    }


    @ApiOperation(value = "订单分页")
    @GetMapping("/pages")
    public Result<PageVO<MallOrderInfo>> pages(OrderPageQuery orderPageQuery) {
        orderPageQuery.setUserId(UserUtils.getUserId());
        Page<MallOrder> page = orderService.findPage(orderPageQuery);
        List<MallOrderInfo> shoppingCartMdseInfoList = orderService.list2vo(page.getContent());
        PageVO<MallOrderInfo> pageVo = PageUtils.toPageVO(page, shoppingCartMdseInfoList);
        return Result.success(pageVo);
    }

    @ApiOperation(value = "订单状态列表")
    @GetMapping("/status/lists")
    public Result<List<EnumTypeInfo>> orderStatusLists() {
        return Result.success(OrderStatusEnum.toList());
    }

    @ApiOperation(value = "配送方式列表")
    @GetMapping("/deliveryMethod/lists")
    public Result<List<EnumTypeInfo>> orderDeliveryMethodLists() {
        return Result.success(OrderDeliveryMethodEnum.toList());
    }

    @ApiOperation(value = "支付方式列表")
    @GetMapping("/payMethod/lists")
    public Result<List<EnumTypeInfo>> orderPayMethodLists() {
        return Result.success(OrderPayMethodEnum.toList());
    }
}
