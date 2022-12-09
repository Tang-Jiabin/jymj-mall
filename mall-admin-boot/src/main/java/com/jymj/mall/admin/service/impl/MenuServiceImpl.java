package com.jymj.mall.admin.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.dto.MenuDTO;
import com.jymj.mall.admin.entity.SysMenu;
import com.jymj.mall.admin.entity.SysRoleMenu;
import com.jymj.mall.admin.repository.SysMenuRepository;
import com.jymj.mall.admin.repository.SysRoleMenuRepository;
import com.jymj.mall.admin.service.MenuService;
import com.jymj.mall.admin.vo.MenuInfo;
import com.jymj.mall.common.constants.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 菜单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final SysMenuRepository menuRepository;
    private final SysRoleMenuRepository roleMenuRepository;

    @Override
    public List<SysMenu> findAllById(List<Long> menuIdList) {
        return menuRepository.findAllByMenuIdInAndDeleted(menuIdList, SystemConstants.DELETED_NO);

    }

    @Override
    public List<SysMenu> findAllByRoleIdIn(List<Long> roleIdList) {
        List<SysRoleMenu> roleMenuList = roleMenuRepository.findAllByRoleIdIn(roleIdList);
        return findAllById(roleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));
    }

    @Override
    public List<MenuInfo> list2tree(List<SysMenu> menuList, long pid) {
        List<MenuInfo> menuInfoList = Lists.newArrayList();
        Optional.ofNullable(menuList).orElse(Lists.newArrayList()).stream().filter(menu -> menu.getParentId().equals(pid)).forEach(sysMenu -> {
            MenuInfo menuInfo = menu2vo(sysMenu);
            menuInfo.setChildren(list2tree(menuList, sysMenu.getMenuId()));
            menuInfoList.add(menuInfo);
        });
        return menuInfoList;
    }

    @Override
    public List<SysMenu> findAllByAdminId(Long adminId) {
        return null;
    }

    public MenuInfo menu2vo(SysMenu sysMenu) {
        MenuInfo menuInfo = new MenuInfo();
        menuInfo.setMenuId(sysMenu.getMenuId());
        menuInfo.setParentId(sysMenu.getParentId());
        menuInfo.setName(sysMenu.getName());
        menuInfo.setIcon(sysMenu.getIcon());
        menuInfo.setPath(sysMenu.getPath());
        menuInfo.setComponent(sysMenu.getComponent());
        menuInfo.setSort(sysMenu.getSort());
        menuInfo.setVisible(sysMenu.getVisible());
        menuInfo.setRedirect(sysMenu.getRedirect());
        menuInfo.setType(sysMenu.getType());
        return menuInfo;
    }

    @Override
    public SysMenu add(MenuDTO dto) {
        return null;
    }

    @Override
    public Optional<SysMenu> update(MenuDTO dto) {
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            menuRepository.deleteAllById(idList);
        }
    }

    @Override
    public Optional<SysMenu> findById(Long id) {
        return menuRepository.findById(id);
    }

    @Override
    public MenuInfo entity2vo(SysMenu entity) {
        return null;
    }

    @Override
    public List<MenuInfo> list2vo(List<SysMenu> entityList) {
        return list2tree(entityList, SystemConstants.ROOT_MENU_ID);
    }
}
