package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.UpdateDeptDTO;
import com.jymj.mall.admin.entity.SysDept;
import com.jymj.mall.admin.service.DeptService;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
    public Result<DeptInfo> addDept(@Valid @RequestBody UpdateDeptDTO deptDTO) {
        SysDept sysDept = deptService.add(deptDTO);
        DeptInfo deptInfo = deptService.entity2vo(sysDept);
        return Result.success(deptInfo);
    }

    @ApiOperation(value = "删除部门")
    @DeleteMapping("/{ids}")
    public Result deleteDept(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        deptService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改部门")
    @PutMapping
    public Result updateDept(@RequestBody UpdateDeptDTO updateDeptDTO) {
        Optional<SysDept> sysDeptOptional = deptService.update(updateDeptDTO);
        return sysDeptOptional.map(dept -> Result.success(deptService.entity2vo(dept))).orElse(Result.failed());
    }

    @ApiOperation(value = "部门信息")
    @GetMapping("/{deptId}/info")
    public Result<DeptInfo> getDeptById(@PathVariable Long deptId) {
        Optional<SysDept> deptOptional = deptService.findById(deptId);
        return deptOptional.map(dept -> Result.success(deptService.entity2vo(dept))).orElse(Result.failed());
    }

    @ApiOperation(value = "子级列表")
    @GetMapping("/{deptId}/children")
    public Result<List<DeptInfo>> children(@PathVariable Long deptId) {
        List<SysDept> deptList = deptService.findChildren(deptId);
        List<DeptInfo> deptInfoList = deptService.list2vo(deptList);
        return Result.success(deptInfoList);
    }

    @ApiOperation(value = "树列表")
    @GetMapping("/{deptId}/tree")
    public Result<List<DeptInfo>> tree(@PathVariable Long deptId) {
        List<SysDept> deptList = deptService.tree(deptId);
        List<DeptInfo> deptInfoList = deptService.list2vo(deptList);
        return Result.success(deptInfoList);
    }
}
