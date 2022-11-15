package com.jymj.mall.order.api;


import com.jymj.mall.common.result.Result;
import com.jymj.mall.order.dto.OrderPaySuccess;
import com.jymj.mall.order.vo.MallOrderInfo;
import com.jymj.mall.shop.dto.VerifyOrderMdse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-10
 */
@FeignClient(value = "mall-order", contextId = "mall-order")
public interface OrderFeignClient {

    @GetMapping("/api/v1/admin/order/no/{orderNo}/info")
    Result<MallOrderInfo> getOrderByNo(@PathVariable String orderNo);

    @PutMapping("/api/v1/admin/order/pay/success")
    Result<Object> paySuccess(@RequestBody OrderPaySuccess orderPaySuccess);

    @GetMapping("/api/v1/admin/order/id/{orderId}/info")
    Result<MallOrderInfo> getOrderById(@PathVariable Long orderId);

    @PutMapping("/api/v1/admin/order/verify")
    Result<Object> verify(@RequestBody VerifyOrderMdse verifyOrderMdse);
}
