package com.jymj.mall.pay.api;

import com.jymj.mall.common.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-03-02
 */

@FeignClient(value = "mall-pay", contextId = "mall-pay")
public interface PayFeignClient {

    @ApiOperation(value = "退款")
    @PostMapping("/api/v1/wx/refund")
    Result refund(@RequestParam String orderNo);
}
