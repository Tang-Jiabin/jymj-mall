package com.jymj.mall.admin.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.dto.AddAdminDTO;
import com.jymj.mall.admin.entity.*;
import com.jymj.mall.admin.repository.SysRoleMenuRepository;
import com.jymj.mall.admin.service.DeptService;
import com.jymj.mall.admin.service.MenuService;
import com.jymj.mall.admin.service.RoleService;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.admin.vo.MenuInfo;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.common.constants.SecurityConstants;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.admin.repository.SysAdminRoleRepository;
import com.jymj.mall.admin.service.AdminService;
import com.jymj.mall.admin.repository.SysAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysAdminRepository adminRepository;
    private final SysAdminRoleRepository adminRoleRepository;
    private final SysRoleMenuRepository roleMenuRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final DeptService deptService;
    private final MenuService menuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddAdminDTO adminDTO) {

        List<SysAdmin> adminList = adminRepository.findAllByUsernameOrMobile(adminDTO.getUsername(), adminDTO.getMobile());
        if (adminList != null && adminList.size() > 0) {
            throw new BusinessException("用户名或手机号已存在");
        }

        SysAdmin admin = new SysAdmin();
        BeanUtils.copyProperties(adminDTO, admin);
        admin.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        admin = adminRepository.save(admin);

        Long addAdminId = admin.getAdminId();
        Long adminId = UserUtils.getAdminId();
        List<SysAdminRole> adminRoles = adminRoleRepository.findAllByAdminId(adminId);
        List<Long> roleIdList = adminRoles.stream().map(SysAdminRole::getRoleId).filter(Objects::nonNull).collect(Collectors.toList());

        List<SysAdminRole> adminRoleList = Lists.newArrayList();
        Optional.ofNullable(adminDTO.getRoleIdList())
                .orElse(Lists.newArrayList())
                .stream()
                .filter(roleIdList::contains)
                .forEach(roleId -> {
                    SysAdminRole adminRole = new SysAdminRole();
                    adminRole.setAdminId(addAdminId);
                    adminRole.setRoleId(roleId);
                    adminRole.setDeleted(0);
                    adminRoleList.add(adminRole);
                });

        adminRoleRepository.saveAll(adminRoleList);
    }

    @Override
    public AdminAuthDTO getAuthInfoByUsername(String username) {
        Optional<SysAdmin> adminOptional = adminRepository.findByUsernameAndDeleted(username, SystemConstants.DELETED_NO);
        AdminAuthDTO adminAuthDTO = null;
        if (adminOptional.isPresent()) {
            SysAdmin admin = adminOptional.get();
            adminAuthDTO = new AdminAuthDTO();
            adminAuthDTO.setUserId(admin.getAdminId());
            adminAuthDTO.setUsername(admin.getUsername());
            adminAuthDTO.setPassword(SecurityConstants.PASSWORD_ENCODE + admin.getPassword());
            adminAuthDTO.setStatus(admin.getStatus());
            adminAuthDTO.setDeptId(admin.getDeptId());
            List<SysAdminRole> adminRoleList = adminRoleRepository.findAllByAdminId(admin.getAdminId());
            List<Long> roleIdList = adminRoleList.stream().map(SysAdminRole::getRoleId).collect(Collectors.toList());
            List<SysRole> roleList = roleService.findAllById(roleIdList);
            List<String> roleStrList = Lists.newArrayList();
            roleList.forEach(sysRole -> roleStrList.add(sysRole.getCode()));
            adminAuthDTO.setRoles(roleStrList);
        }

        return adminAuthDTO;
    }

    @Override
    public Optional<SysAdmin> findById(Long adminId) {
        return adminRepository.findByAdminIdAndDeleted(adminId, SystemConstants.DELETED_NO);

    }

    @Override
    public AdminInfo admin2vo(SysAdmin admin) {
        AdminInfo adminInfo = new AdminInfo();
        adminInfo.setAdminId(admin.getAdminId());
        adminInfo.setUsername(admin.getUsername());
        adminInfo.setNickname(admin.getNickname());
        adminInfo.setMobile(admin.getMobile());
        adminInfo.setGender(admin.getGender());
        adminInfo.setAvatar(admin.getAvatar());
        adminInfo.setEmail(admin.getEmail());
        adminInfo.setStatus(admin.getStatus());

        List<SysRole> roleList = roleService.findAllByAdminId(admin.getAdminId());
        List<RoleInfo> roleInfoList = roleService.list2vo(roleList);
        adminInfo.setRoleInfoList(roleInfoList);

        Optional<SysDept> deptOptional = deptService.findById(admin.getDeptId());
        deptOptional.ifPresent(dept->adminInfo.setDeptInfo(deptService.dept2vo(dept)));

        List<SysMenu> menuList = menuService.findAllByRoleIdIn(roleList.stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        List<MenuInfo> menuInfoList = menuService.list2tree(menuList,SystemConstants.ROOT_MENU_ID);
        adminInfo.setMenuInfoList(menuInfoList);

        return adminInfo;
    }
}
