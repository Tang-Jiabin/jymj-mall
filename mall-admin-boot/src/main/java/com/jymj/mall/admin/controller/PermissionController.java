package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.service.PermissionService;
import com.jymj.mall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        permissionService.refreshPermRolesRules();
        return Result.success();
    }

}
