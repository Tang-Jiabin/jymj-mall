package com.jymj.mall.admin.api;

import com.jymj.mall.admin.vo.DistrictInfo;
import com.jymj.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

/**
 * 权限
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-25
 */
@FeignClient(value = "mall-admin", contextId = "mall-permission")
public interface PermissionFeignClient {


    @PutMapping("/api/v1/permission/refresh")
    Result refresh();
}
