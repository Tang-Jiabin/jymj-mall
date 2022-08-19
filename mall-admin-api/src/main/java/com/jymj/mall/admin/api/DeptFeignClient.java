package com.jymj.mall.admin.api;

import com.jymj.mall.admin.dto.AddDeptDTO;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@FeignClient(value = "mall-admin",contextId = "mall-dept")
public interface DeptFeignClient {

    @PostMapping("/api/v1/dept")
    Result<DeptInfo> add(AddDeptDTO addDeptDTO);
}
