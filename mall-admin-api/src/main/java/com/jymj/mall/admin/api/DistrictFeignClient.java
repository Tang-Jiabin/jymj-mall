package com.jymj.mall.admin.api;

import com.jymj.mall.admin.vo.DistrictInfo;
import com.jymj.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 行政区
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@FeignClient(value = "mall-admin", contextId = "mall-district")
public interface DistrictFeignClient {

    @GetMapping("/api/v1/district/{districtId}/children")
    Result<List<DistrictInfo>> children(@PathVariable Long districtId);
    @GetMapping("/api/v1/district/{districtId}/parent")
    Result<List<DistrictInfo>> parent(@PathVariable Long districtId);
}
