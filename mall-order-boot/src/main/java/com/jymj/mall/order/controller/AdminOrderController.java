package com.jymj.mall.order.controller;

import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.order.dto.OrderPaySuccess;
import com.jymj.mall.order.entity.MallOrder;
import com.jymj.mall.order.service.OrderService;
import com.jymj.mall.order.vo.MallOrderInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 管理员订单
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-12
 */
@Api(tags = "管理员订单")
@RestController
@RequestMapping("/api/v1/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @ApiOperation(value = "支付成功")
    @PutMapping("/pay/success")
    public Result<Object> paySuccess(@RequestBody OrderPaySuccess orderPaySuccess) {
        orderService.paySuccess(orderPaySuccess);
        return Result.success();
    }

    @ApiOperation(value = "订单信息")
    @GetMapping("/no/{orderNo}/info")
    public Result<MallOrderInfo> getOrderByNo(@PathVariable String orderNo) {
        Optional<MallOrder> orderOptional = orderService.findByOrderNo(orderNo);
        MallOrder mallOrder = orderOptional.orElseThrow(() -> new BusinessException("订单不存在"));
        MallOrderInfo orderInfo = orderService.entity2vo(mallOrder);
        return Result.success(orderInfo);
    }
}
