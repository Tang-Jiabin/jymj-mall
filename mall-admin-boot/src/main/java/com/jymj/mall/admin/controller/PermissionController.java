package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.UpdatePerm;
import com.jymj.mall.admin.entity.SysPermission;
import com.jymj.mall.admin.service.PermissionService;
import com.jymj.mall.admin.vo.PermInfo;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 权限
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-25
 */
@Api(tags = "权限")
@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;


    @ApiOperation(value = "刷新权限缓存")
    @PutMapping(value = "/refresh")
    public Result refreshPermRolesRules() {
        boolean result = permissionService.refreshPermRolesRules();
        return Result.judge(result);
    }

    @ApiOperation(value = "添加权限")
    @PostMapping
    public Result<PermInfo> addPerm(@Valid @RequestBody UpdatePerm updatePerm) {
        SysPermission permission = permissionService.add(updatePerm);
        PermInfo permInfo = permissionService.entity2vo(permission);
        return Result.success(permInfo);
    }

    @ApiOperation(value = "删除权限")
    @DeleteMapping("/{ids}")
    public Result deletePerm(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        permissionService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改权限")
    @PutMapping
    public Result<PermInfo> updatePerm(@RequestBody UpdatePerm updatePerm) {
        Optional<SysPermission> permissionOptional = permissionService.update(updatePerm);
        return permissionOptional
                .map(permission -> Result.success(permissionService.entity2vo(permission)))
                .orElseGet(() -> Result.failed("权限信息不存在"));

    }

    @ApiOperation(value = "权限信息")
    @GetMapping("/{permId}/info")
    public Result<PermInfo> getPermById(@PathVariable Long permId) {
        Optional<SysPermission> permissionOptional = permissionService.findById(permId);
        return permissionOptional
                .map(permission -> Result.success(permissionService.entity2vo(permission)))
                .orElseGet(() -> Result.failed("权限信息不存在"));
    }

    @ApiOperation(value = "权限列表")
    @GetMapping("/list")
    public Result<List<PermInfo>> permList() {
        Long adminId = UserUtils.getAdminId();
        List<SysPermission> permissionList = permissionService.findAllByAdminId(adminId);
        List<PermInfo> permInfoList = permissionService.list2vo(permissionList);
        return Result.success(permInfoList);
    }


}
