package com.jymj.mall.user.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.user.vo.AddressInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户地址
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
@FeignClient(value = "mall-user", contextId = "mall-address")
public interface UserAddressFeignClient {

    @GetMapping("/api/v1/address/{addressId}/info")
    Result<AddressInfo> getAddressById(@PathVariable Long addressId);
}
