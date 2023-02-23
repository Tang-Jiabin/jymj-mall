package com.jymj.mall.marketing.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.marketing.dto.UserCouponDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-23
 */
@FeignClient(value = "mall-marketing", contextId = "mall-marketing")
public interface MarketingFeignClient {

    @PostMapping("/api/v1/admin/coupon/user")
    Result<Object> addUserCoupon(@RequestBody UserCouponDTO userCouponDTO);

    @PutMapping("/api/v1/admin/coupon/user")
    Result<Object> updateUserCoupon(@RequestBody UserCouponDTO userCouponDTO);

    @DeleteMapping("/api/v1/admin/coupon/user/{ids}")
    Result<Object> deleteUserCoupon(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids);
}
