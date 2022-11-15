package com.jymj.mall.shop.api;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.shop.vo.VerifyPersonInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;

/**
 * 核销
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-27
 */
@FeignClient(value = "mall-shop", contextId = "mall-verify")
public interface VerifyFeignClient {

    @GetMapping("/api/v1/verification/id/{verifyPersonId}/info")
    Result<VerifyPersonInfo> getVerifyPersonById(@Valid @PathVariable Long verifyPersonId);

    @GetMapping("/api/v1/verification/adminId/{adminId}/info")
    Result<VerifyPersonInfo> getVerifyPersonByAdminId(@Valid @PathVariable Long adminId);

    @GetMapping("/api/v1/verification/userId/{userId}/info")
    Result<VerifyPersonInfo> getVerifyPersonByUserId(@Valid @PathVariable Long userId);

    @DeleteMapping("/api/v1/verification/adminId/{ids}")
    Result<String> deleteVerifyPersonByAdminIds(@Valid @PathVariable String ids);
}
