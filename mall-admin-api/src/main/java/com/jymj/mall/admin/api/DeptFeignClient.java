package com.jymj.mall.admin.api;

import com.jymj.mall.admin.dto.AddDeptDTO;
import com.jymj.mall.admin.dto.UpdateDeptDTO;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@FeignClient(value = "mall-admin", contextId = "mall-dept")
public interface DeptFeignClient {

    @PostMapping("/api/v1/dept")
    Result<DeptInfo> add(@RequestBody AddDeptDTO addDeptDTO);

    @PutMapping("/api/v1/dept")
    Result updateDept(@RequestBody UpdateDeptDTO updateDeptDTO);


    @GetMapping("/api/v1/dept/{deptId}/info")
    Result<DeptInfo> getDeptById(@PathVariable Long deptId);

    @GetMapping("/api/v1/dept/{deptId}/children")
    public Result<List<DeptInfo>> children(@PathVariable Long deptId);

    @GetMapping("/api/v1/dept/{deptId}/tree")
     Result<List<DeptInfo>> tree(@PathVariable Long deptId);


}
