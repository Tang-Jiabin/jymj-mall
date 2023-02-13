package com.jymj.mall.admin.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.dto.MenuDTO;
import com.jymj.mall.admin.entity.SysAdminRole;
import com.jymj.mall.admin.entity.SysMenu;
import com.jymj.mall.admin.entity.SysRoleMenu;
import com.jymj.mall.admin.repository.SysAdminRoleRepository;
import com.jymj.mall.admin.repository.SysMenuRepository;
import com.jymj.mall.admin.repository.SysRoleMenuRepository;
import com.jymj.mall.admin.service.MenuService;
import com.jymj.mall.admin.vo.MenuInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
    private final SysAdminRoleRepository adminRoleRepository;

    @Override
    public List<SysMenu> findAllById(List<Long> menuIdList) {
        return menuRepository.findAllByMenuIdIn(menuIdList);
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
        List<SysAdminRole> adminRoleList = adminRoleRepository.findAllByAdminId(adminId);
        if (!CollectionUtils.isEmpty(adminRoleList)) {
            return findAllByRoleIdIn(adminRoleList.stream().map(SysAdminRole::getRoleId).collect(Collectors.toList()));
        }
        return Lists.newArrayList();
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
        SysMenu sysMenu = new SysMenu();
        sysMenu.setParentId(dto.getParentId());
        sysMenu.setName(dto.getName());
        sysMenu.setIcon(dto.getIcon());
        sysMenu.setPath(dto.getPath());
        sysMenu.setComponent(dto.getComponent());
        sysMenu.setSort(dto.getSort());
        sysMenu.setVisible(dto.getVisible());
        sysMenu.setRedirect(dto.getRedirect());
        sysMenu.setType(dto.getType());
        return menuRepository.save(sysMenu);
    }

    @Override
    public Optional<SysMenu> update(MenuDTO dto) {
        Assert.notNull(dto.getMenuId(), "菜单ID不能为空");
        Optional<SysMenu> sysMenuOptional = menuRepository.findById(dto.getMenuId());

        SysMenu sysMenu = sysMenuOptional.orElseThrow(() -> new BusinessException("菜单不存在"));
        if (!ObjectUtils.isEmpty(dto.getParentId())) {
            sysMenu.setParentId(dto.getParentId());
        }
        if (StringUtils.hasText(dto.getName())) {
            sysMenu.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getIcon())) {
            sysMenu.setIcon(dto.getIcon());
        }
        if (StringUtils.hasText(dto.getPath())) {
            sysMenu.setPath(dto.getPath());
        }
        if (StringUtils.hasText(dto.getComponent())) {
            sysMenu.setComponent(dto.getComponent());
        }
        if (!ObjectUtils.isEmpty(dto.getSort())) {
            sysMenu.setSort(dto.getSort());
        }
        if (!ObjectUtils.isEmpty(dto.getVisible())) {
            sysMenu.setVisible(dto.getVisible());
        }
        if (StringUtils.hasText(dto.getRedirect())) {
            sysMenu.setRedirect(dto.getRedirect());
        }
        if (!ObjectUtils.isEmpty(dto.getType())) {
            sysMenu.setType(dto.getType());
        }
        return Optional.of(menuRepository.save(sysMenu));
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
        return menu2vo(entity);
    }

    @Override
    public List<MenuInfo> list2vo(List<SysMenu> entityList) {
        return list2tree(entityList, SystemConstants.ROOT_MENU_ID);
    }
}
