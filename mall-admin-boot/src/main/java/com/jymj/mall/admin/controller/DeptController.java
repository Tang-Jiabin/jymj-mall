package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.AddDeptDTO;
import com.jymj.mall.admin.dto.UpdateDeptDTO;
import com.jymj.mall.admin.entity.SysDept;
import com.jymj.mall.admin.service.DeptService;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * 部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@Api(tags = "部门")
@RequestMapping("/api/v1/dept")
@RestController
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @ApiOperation(value = "添加部门")
    @PostMapping
    public Result<DeptInfo> addDept(@Valid @RequestBody AddDeptDTO deptDTO) {
        SysDept sysDept = deptService.add(deptDTO);
        DeptInfo deptInfo = deptService.dept2vo(sysDept);
        return Result.success(deptInfo);
    }

    @ApiOperation(value = "删除部门")
    @DeleteMapping("/{ids}")
    public Result deleteDept(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        deptService.deleteDept(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改部门")
    @PutMapping
    public Result updateDept(@Valid @RequestBody UpdateDeptDTO updateDeptDTO) {
        deptService.updateDept(updateDeptDTO);
        return Result.success();
    }

    @ApiOperation(value = "部门信息")
    @GetMapping("/{deptId}/info")
    public Result<DeptInfo> getDeptById(@PathVariable Long deptId) {
        Optional<SysDept> deptOptional = deptService.findById(deptId);
        SysDept sysDept = deptOptional.orElseThrow(() -> new BusinessException("部门不存在"));
        DeptInfo deptInfo = deptService.dept2vo(sysDept);
        return Result.success(deptInfo);
    }
}
