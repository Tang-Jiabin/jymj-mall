package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.MenuDTO;
import com.jymj.mall.admin.entity.SysMenu;
import com.jymj.mall.admin.service.MenuService;
import com.jymj.mall.admin.vo.MenuInfo;
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
 * 菜单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-12-07
 */
@Api(tags = "菜单")
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @ApiOperation(value = "添加菜单")
    @PostMapping
    public Result<MenuInfo> addMenu(@Valid @RequestBody MenuDTO menuDTO) {
        SysMenu sysMenu = menuService.add(menuDTO);
        MenuInfo menuInfo = menuService.entity2vo(sysMenu);
        return Result.success(menuInfo);
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteMenu(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        menuService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping
    public Result<MenuInfo> updateMenu(@RequestBody MenuDTO menuDTO) {
        Optional<SysMenu> sysMenuOptional = menuService.update(menuDTO);
        return sysMenuOptional
                .map(sysMenu -> Result.success(menuService.entity2vo(sysMenu)))
                .orElseGet(() -> Result.failed("更新失败"));

    }


    @ApiOperation(value = "菜单列表")
    @GetMapping("/list")
    public Result<List<MenuInfo>> menuList() {
        Long adminId = UserUtils.getAdminId();
        List<SysMenu> menuList = menuService.findAllByAdminId(adminId);
        List<MenuInfo> menuInfoList = menuService.list2vo(menuList);
        return Result.success(menuInfoList);
    }


}
