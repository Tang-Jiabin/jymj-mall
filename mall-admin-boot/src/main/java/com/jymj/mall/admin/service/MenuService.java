package com.jymj.mall.admin.service;

import com.jymj.mall.admin.entity.SysMenu;
import com.jymj.mall.admin.vo.MenuInfo;

import java.util.List;

/**
 * 菜单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
public interface MenuService {
    List<SysMenu> findAllById(List<Long> menuIdList);

    List<SysMenu> findAllByRoleIdIn(List<Long> roleIdList);

    List<MenuInfo> list2tree(List<SysMenu> menuList,long pid);
}
