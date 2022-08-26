package com.jymj.mall.admin.service.impl;

import com.jymj.mall.admin.dto.RoleFormDTO;
import com.jymj.mall.admin.dto.RoleResourceFormDTO;
import com.jymj.mall.admin.entity.SysAdminRole;
import com.jymj.mall.admin.entity.SysRole;
import com.jymj.mall.admin.repository.SysAdminRoleRepository;
import com.jymj.mall.admin.repository.SysRoleRepository;
import com.jymj.mall.admin.service.RoleService;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.web.vo.OptionVO;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

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
    private final SysAdminRoleRepository adminRoleRepository;

    @Override
    public List<OptionVO> listRoleOptions() {
        return null;
    }

    @Override
    public boolean saveRole(RoleFormDTO roleForm) {
        return false;
    }

    @Override
    public SysRole getById(Long roleId) {
        return null;
    }

    @Override
    public boolean deleteRoles(String ids) {
        return false;
    }

    @Override
    public boolean updateRoleStatus(Long roleId, Integer status) {
        return false;
    }

    @Override
    public RoleResourceFormDTO getRoleResources(Long roleId) {
        return null;
    }

    @Override
    public boolean updateRoleResource(Long roleId, RoleResourceFormDTO roleResourceForm) {
        return false;
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
        adminRoleRepository.deleteAllByAdminIdAndRoleIdIn(adminId, deleteRoleList);
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

    public RoleInfo role2vo(SysRole role) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRoleId(role.getRoleId());
        roleInfo.setName(role.getName());
        roleInfo.setCode(role.getCode());
        roleInfo.setSort(role.getSort());
        roleInfo.setStatus(role.getStatus());
        return roleInfo;
    }
}
