package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.RoleResourceFormDTO;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.admin.service.RoleService;
import com.jymj.mall.admin.dto.RoleFormDTO;
import com.jymj.mall.admin.entity.SysRole;
import com.jymj.mall.common.web.vo.OptionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Api(tags = "角色")
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService sysRoleService;

    @ApiOperation(value = "角色分页列表")
    @GetMapping("/pages")
    public Result listRolePages() {

        return Result.success();
    }

    @ApiOperation(value = "角色下拉列表")
    @GetMapping("/options")
    public Result<List<OptionVO>> listRoleOptions() {
        List<OptionVO> list = sysRoleService.listRoleOptions();
        return Result.success(list);
    }

    @ApiOperation(value = "角色详情")
    @GetMapping("/{roleId}")
    public Result getRoleDetail(@ApiParam("角色ID") @PathVariable Long roleId) {
        SysRole role = sysRoleService.getById(roleId);
        return Result.success(role);
    }

    @ApiOperation(value = "新增角色")
    @PostMapping
    public Result addRole(@Valid @RequestBody RoleFormDTO roleForm) {
        boolean result = sysRoleService.saveRole(roleForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改角色")
    @PutMapping(value = "/{id}")
    public Result updateRole(@Valid @RequestBody RoleFormDTO roleForm) {
        boolean result = sysRoleService.saveRole(roleForm);
        return Result.judge(result);
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/{ids}")
    public Result deleteRoles(@ApiParam("删除角色，多个以英文逗号(,)分割") @PathVariable String ids) {
        boolean result = sysRoleService.deleteRoles(ids);
        return Result.judge(result);
    }

    @ApiOperation(value = "修改角色状态")
    @PutMapping(value = "/{roleId}/status")
    public Result updateRoleStatus(@ApiParam("角色ID") @PathVariable Long roleId, @ApiParam("角色状态:1-正常；0-禁用") @RequestParam Integer status) {
        boolean result = sysRoleService.updateRoleStatus(roleId, status);
        return Result.judge(result);
    }

    @ApiOperation(value = "获取角色的资源ID集合", notes = "资源包括菜单和权限ID")
    @GetMapping("/{roleId}/resources")
    public Result<RoleResourceFormDTO> getRoleResources(@ApiParam("角色ID") @PathVariable Long roleId) {
        RoleResourceFormDTO resourceIds = sysRoleService.getRoleResources(roleId);
        return Result.success(resourceIds);
    }

    @ApiOperation(value = "分配角色的资源权限")
    @PutMapping("/{roleId}/resources")
    public Result updateRoleResource(@PathVariable Long roleId, @RequestBody RoleResourceFormDTO roleResourceForm) {
        boolean result = sysRoleService.updateRoleResource(roleId, roleResourceForm);
        return Result.judge(result);
    }
}
