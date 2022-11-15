package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.AddRole;
import com.jymj.mall.admin.dto.RolePageQuery;
import com.jymj.mall.admin.dto.RoleResource;
import com.jymj.mall.admin.dto.UpdateRole;
import com.jymj.mall.admin.entity.SysRole;
import com.jymj.mall.admin.service.RoleService;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.OptionVO;
import com.jymj.mall.common.web.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    @ApiOperation(value = "新增角色")
    @PostMapping
    public Result<RoleInfo> addRole(@Valid @RequestBody AddRole addRole) {
        SysRole result = sysRoleService.addRole(addRole);
        RoleInfo roleInfo = sysRoleService.entity2vo(result);
        return Result.success(roleInfo);
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/{ids}")
    public Result deleteRoles(@ApiParam("删除角色，多个以英文逗号(,)分割") @PathVariable String ids) {
        sysRoleService.deleteRoles(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改角色")
    @PutMapping
    public Result<SysRole> updateRole(@Valid @RequestBody UpdateRole updateRole) {
        SysRole result = sysRoleService.updateRole(updateRole);
        return Result.success(result);
    }

    @ApiOperation(value = "角色详情")
    @GetMapping("/id/{roleId}")
    public Result<RoleInfo> getRoleDetail(@ApiParam("角色ID") @PathVariable Long roleId) {
        Optional<SysRole> roleOptional = sysRoleService.getById(roleId);
        return roleOptional.map(role -> Result.success(sysRoleService.entity2vo(role))).orElseGet(() -> Result.failed("角色不存在"));
    }

    @ApiOperation(value = "角色详情")
    @GetMapping("/ids/{ids}")
    public Result<List<RoleInfo>> getRoleDetailList(@ApiParam("角色ID") @PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<SysRole> sysRoleList = sysRoleService.findAllById(idList);
        List<RoleInfo> roleInfoList = sysRoleService.list2vo(sysRoleList);
        return Result.success(roleInfoList);
    }

    @ApiOperation(value = "角色下拉列表")
    @GetMapping("/options")
    public Result<List<OptionVO<Long>>> listRoleOptions() {
        List<OptionVO<Long>> list = sysRoleService.listRoleOptions();
        return Result.success(list);
    }

    @ApiOperation(value = "角色分页列表")
    @GetMapping("/pages")
    public Result<PageVO<RoleInfo>> rolePages(@Valid RolePageQuery rolePageQuery) {
        Page<SysRole> sysRolePage = sysRoleService.findPage(rolePageQuery);
        List<RoleInfo> roleInfoList = sysRoleService.list2vo(sysRolePage.getContent());
        PageVO<RoleInfo> roleInfoPage = PageUtils.toPageVO(sysRolePage, roleInfoList);
        return Result.success(roleInfoPage);
    }


    @ApiOperation(value = "获取角色的资源ID集合", notes = "资源包括菜单和权限ID")
    @GetMapping("/{roleId}/resources")
    public Result<RoleResource> getRoleResources(@ApiParam("角色ID") @PathVariable Long roleId) {
        RoleResource resourceIds = sysRoleService.getRoleResources(roleId);
        return Result.success(resourceIds);
    }

    @ApiOperation(value = "分配角色的资源权限")
    @PutMapping("/{roleId}/resources")
    public Result updateRoleResource(@PathVariable Long roleId, @RequestBody RoleResource roleResourceForm) {
        sysRoleService.updateRoleResource(roleId, roleResourceForm);
        return Result.success();
    }
}
