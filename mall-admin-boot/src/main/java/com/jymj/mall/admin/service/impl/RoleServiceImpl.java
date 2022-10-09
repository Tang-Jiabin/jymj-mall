package com.jymj.mall.admin.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.dto.AddRole;
import com.jymj.mall.admin.dto.RolePageQuery;
import com.jymj.mall.admin.dto.RoleResource;
import com.jymj.mall.admin.dto.UpdateRole;
import com.jymj.mall.admin.entity.*;
import com.jymj.mall.admin.repository.SysAdminRoleRepository;
import com.jymj.mall.admin.repository.SysRoleMenuRepository;
import com.jymj.mall.admin.repository.SysRolePermissionRepository;
import com.jymj.mall.admin.repository.SysRoleRepository;
import com.jymj.mall.admin.service.DeptService;
import com.jymj.mall.admin.service.RoleService;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.common.web.vo.OptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleRepository roleRepository;
    private final DeptService deptService;
    private final SysAdminRoleRepository adminRoleRepository;
    private final SysRolePermissionRepository rolePermissionRepository;
    private final SysRoleMenuRepository roleMenuRepository;

    @Override
    public List<OptionVO<Long>> listRoleOptions() {
        Long deptId = UserUtils.getDeptId();
        List<SysRole> sysRoleList = roleRepository.findAllByDeptId(deptId);
        List<OptionVO<Long>> optionVOList = Lists.newArrayList();
        Optional.ofNullable(sysRoleList).orElse(Lists.newArrayList()).forEach(role -> {
            OptionVO<Long> option = new OptionVO<>(role.getRoleId(), role.getName());
            optionVOList.add(option);
        });
        return optionVOList;
    }

    @Override
    public SysRole addRole(AddRole addRole) {
        Long deptId = UserUtils.getDeptId();
        List<SysDept> deptList = deptService.findChildren(deptId);
        if (!deptList.stream().map(SysDept::getDeptId).collect(Collectors.toList()).contains(addRole.getDeptId())){
            throw new BusinessException("部门权限不足");
        }
        String code = addRole.getDeptId() + "_" + addRole.getCode();
        Optional<SysRole> roleOptional = roleRepository.findByCode(code);
        if (roleOptional.isPresent()) {
            throw new BusinessException("角色编码已存在");
        }
        SysRole role = new SysRole();
        role.setName(addRole.getName());
        role.setCode(code);
        role.setDescribe(addRole.getDescribe());
        role.setSort(addRole.getSort());
        role.setStatus(addRole.getStatus());
        role.setDeleted(SystemConstants.DELETED_NO);
        role.setDeptId(addRole.getDeptId());
        return roleRepository.save(role);

    }

    @Override
    public Optional<SysRole> getById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    public void deleteRoles(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<SysRole> sysRoleList = roleRepository.findAllById(idList);
        roleRepository.deleteAll(sysRoleList);
    }

    @Override
    public boolean updateRoleStatus(Long roleId, Integer status) {
        return false;
    }

    @Override
    public RoleResource getRoleResources(Long roleId) {
        RoleResource roleResource = new RoleResource();
        //权限资源列表
        List<SysRolePermission> rolePermissionList = rolePermissionRepository.findAllByRoleId(roleId);
        roleResource.setPermIds(rolePermissionList.stream().map(SysRolePermission::getPermId).collect(Collectors.toList()));

        //菜单资源列表
        List<SysRoleMenu> roleMenuList = roleMenuRepository.findAllByRoleId(roleId);
        roleResource.setMenuIds(roleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));

        return roleResource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleResource(Long roleId, RoleResource roleResourceForm) {

        //权限资源
        List<Long> permIds = roleResourceForm.getPermIds();
        if (!CollectionUtils.isEmpty(permIds)) {
            List<SysRolePermission> rolePermissionList = rolePermissionRepository.findAllByRoleId(roleId);
            List<SysRolePermission> deleteRolePerm = rolePermissionList.stream().filter(sysRolePermission -> !permIds.contains(sysRolePermission.getPermId())).collect(Collectors.toList());
            List<SysRolePermission> addRolePerm = permIds.stream()
                    .filter(id -> !rolePermissionList.stream().map(SysRolePermission::getPermId).collect(Collectors.toList()).contains(id))
                    .map(permId -> {
                        SysRolePermission rolePermission = new SysRolePermission();
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermId(permId);
                        rolePermission.setDeleted(SystemConstants.DELETED_NO);
                        return rolePermission;
                    }).collect(Collectors.toList());

            rolePermissionRepository.deleteAll(deleteRolePerm);
            rolePermissionRepository.saveAll(addRolePerm);
        }

        //菜单资源
        List<Long> menuIds = roleResourceForm.getMenuIds();
        if (!CollectionUtils.isEmpty(menuIds)) {
            List<SysRoleMenu> roleMenuList = roleMenuRepository.findAllByRoleId(roleId);
            List<SysRoleMenu> deleteRoleMenus = roleMenuList.stream().filter(roleMenu -> !menuIds.contains(roleMenu.getMenuId())).collect(Collectors.toList());
            List<SysRoleMenu> addRoleMenus = menuIds.stream()
                    .filter(id -> !roleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()).contains(id))
                    .map(menuId -> {
                       SysRoleMenu roleMenu = new SysRoleMenu();
                       roleMenu.setMenuId(menuId);
                       roleMenu.setRoleId(roleId);
                       roleMenu.setDeleted(SystemConstants.DELETED_NO);
                       return roleMenu;
                    }).collect(Collectors.toList());

            roleMenuRepository.deleteAll(deleteRoleMenus);
            roleMenuRepository.saveAll(addRoleMenus);
        }
    }

    @Override
    public List<SysRole> findAllById(List<Long> ids) {
        return roleRepository.findAllByRoleIdInAndDeleted(ids, SystemConstants.DELETED_NO);
    }

    @Override
    public List<RoleInfo> list2vo(List<SysRole> roleList) {
        List<RoleInfo> roleInfoList = Lists.newArrayList();
        Optional.ofNullable(roleList).orElse(Lists.newArrayList()).stream().filter(Objects::nonNull).forEach(role -> {
            RoleInfo roleInfo = role2vo(role);
            roleInfoList.add(roleInfo);
        });
        return roleInfoList;
    }

    @Override
    public List<SysRole> findAllByAdminId(Long adminId) {
        List<SysAdminRole> adminRoleList = adminRoleRepository.findAllByAdminId(adminId);
        return findAllById(adminRoleList.stream().map(SysAdminRole::getRoleId).collect(Collectors.toList()));

    }

    @Override
    public void deleteAdminRole(Long adminId, List<SysRole> deleteRoleList) {
        adminRoleRepository.deleteAllByAdminIdAndRoleIdIn(adminId, deleteRoleList.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
    }

    @Override
    public void addAdminRole(Long adminId, List<SysRole> addRoleList) {
        List<SysAdminRole> adminRoleList = Lists.newArrayList();
        addRoleList.forEach(role -> {
            SysAdminRole adminRole = new SysAdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(role.getRoleId());
            adminRole.setDeleted(SystemConstants.DELETED_NO);
            adminRoleList.add(adminRole);
        });
        adminRoleRepository.saveAll(adminRoleList);
    }

    @Override
    public SysRole updateRole(UpdateRole updateRole) {


        Optional<SysRole> sysRole = roleRepository.findById(updateRole.getId());
        if (!sysRole.isPresent()) {
            throw new BusinessException("角色不存在");
        }

        SysRole role = sysRole.get();
        if (StringUtils.hasText(updateRole.getName())) {
            role.setName(updateRole.getName());
        }
        if (StringUtils.hasText(updateRole.getDescribe())){
            role.setDescribe(updateRole.getDescribe());
        }
        if (StringUtils.hasText(updateRole.getCode())) {
            role.setCode(updateRole.getCode());
        }
        if (updateRole.getSort() != null) {
            role.setSort(updateRole.getSort());
        }
        if (updateRole.getStatus() != null) {
            role.setStatus(updateRole.getStatus());
        }

        return roleRepository.save(role);
    }

    @Override
    public Page<SysRole> findPage(RolePageQuery rolePageQuery) {

        Long deptId = UserUtils.getDeptId();
        Pageable pageable =PageUtils.getPageable(rolePageQuery);
        return roleRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();


            list.add(criteriaBuilder.equal(root.get("deptId").as(Integer.class), deptId));
            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    @Override
    public List<SysAdminRole> findAdminRoleAllByRoleId(Long roleId) {

        return adminRoleRepository.findAllByRoleId(roleId);
    }

    @Override
    public RoleInfo entity2vo(SysRole role) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRoleId(role.getRoleId());
        roleInfo.setName(role.getName());
        roleInfo.setCode(role.getCode());
        roleInfo.setSort(role.getSort());
        roleInfo.setDescribe(role.getDescribe());
        roleInfo.setStatus(role.getStatus());
        long count = adminRoleRepository.countByRoleId(role.getRoleId());
        roleInfo.setWorkforce(count);
        return roleInfo;
    }


    public RoleInfo role2vo(SysRole role) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRoleId(role.getRoleId());
        roleInfo.setName(role.getName());
        roleInfo.setCode(role.getCode());
        roleInfo.setSort(role.getSort());
        roleInfo.setDescribe(role.getDescribe());
        roleInfo.setStatus(role.getStatus());
        long count = adminRoleRepository.countByRoleId(role.getRoleId());
        roleInfo.setWorkforce(count);
        return roleInfo;
    }
}
