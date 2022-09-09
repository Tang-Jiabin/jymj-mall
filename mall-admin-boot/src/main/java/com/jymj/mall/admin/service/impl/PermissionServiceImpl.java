package com.jymj.mall.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.jymj.mall.admin.dto.PermissionDTO;
import com.jymj.mall.admin.dto.UpdatePerm;
import com.jymj.mall.admin.entity.SysPermission;
import com.jymj.mall.admin.entity.SysRole;
import com.jymj.mall.admin.entity.SysRolePermission;
import com.jymj.mall.admin.repository.SysPermissionRepository;
import com.jymj.mall.admin.repository.SysRolePermissionRepository;
import com.jymj.mall.admin.service.AdminService;
import com.jymj.mall.admin.service.PermissionService;
import com.jymj.mall.admin.service.RoleService;
import com.jymj.mall.admin.vo.PermInfo;
import com.jymj.mall.common.constants.GlobalConstants;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.redis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final RedisUtils redisUtils;
    private final AdminService adminService;
    private final RoleService roleService;
    private final SysPermissionRepository permissionRepository;
    private final SysRolePermissionRepository rolePermissionRepository;

    @Override
    public boolean refreshPermRolesRules() {
        log.info("初始化权限列表");
        redisUtils.del(GlobalConstants.URL_PERM_ROLES_KEY, GlobalConstants.BTN_PERM_ROLES_KEY);

        List<PermissionDTO> permissions = this.allPermRoles();

        if (CollectionUtil.isNotEmpty(permissions)) {
            // 初始化URL【权限->角色(集合)】规则
            List<PermissionDTO> urlPermList = permissions.stream()
                    .filter(item -> StrUtil.isNotBlank(item.getUrlPerm()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(urlPermList)) {
                Map<String, List<String>> urlPermRoles = Maps.newHashMap();
                urlPermList.forEach(item -> {
                    String perm = item.getUrlPerm();
                    urlPermRoles.put(perm, item.getRoles());
                });
                log.info("添加URL拦截规则 : {}", urlPermRoles);
                redisUtils.hset(GlobalConstants.URL_PERM_ROLES_KEY, urlPermRoles);
            }
            // 初始化URL【按钮->角色(集合)】规则
            List<PermissionDTO> btnPermList = permissions.stream()
                    .filter(item -> StrUtil.isNotBlank(item.getBtnPerm()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(btnPermList)) {
                Map<String, List<String>> btnPermRoles = MapUtil.newHashMap();
                btnPermList.forEach(item -> {
                    String perm = item.getBtnPerm();
                    btnPermRoles.put(perm, item.getRoles());
                });
                log.info("添加BTN拦截规则 : {}", btnPermRoles);
                redisUtils.hset(GlobalConstants.URL_PERM_ROLES_KEY, btnPermRoles);
            }
        }
        return true;
    }

    @Override
    public List<SysPermission> findAllByAdminId(Long adminId) {
        List<SysRole> roleList = roleService.findAllByAdminId(adminId);
        List<Long> roleIdList = Optional.of(roleList).orElse(Lists.newArrayList()).stream().map(SysRole::getRoleId).collect(Collectors.toList());
        List<SysRolePermission> rolePermissionList = rolePermissionRepository.findAllByRoleIdIn(roleIdList);
        List<Long> permIdList = Optional.of(rolePermissionList).orElse(Lists.newArrayList()).stream().map(SysRolePermission::getPermId).collect(Collectors.toList());
        return permissionRepository.findAllById(permIdList);
    }

    @Override
    public SysPermission add(UpdatePerm updatePerm) {

        Optional<SysPermission> permissionOptional = permissionRepository.findByUrlPerm(updatePerm.getUrlPerm());
        if (permissionOptional.isPresent()) {
            throw new BusinessException("资源路径已存在");
        }
        SysPermission permission = new SysPermission();
        permission.setName(updatePerm.getName());
        permission.setMenuId(updatePerm.getMenuId());
        permission.setUrlPerm(updatePerm.getUrlPerm());
        permission.setBtnPerm(updatePerm.getBtnPerm());
        permission.setDeleted(SystemConstants.DELETED_NO);
        return permissionRepository.save(permission);

    }

    @Override
    public Optional<SysPermission> update(UpdatePerm dto) {
        if (dto.getPermId() != null) {
            Optional<SysPermission> permissionOptional = findById(dto.getPermId());
            if (permissionOptional.isPresent()) {
                SysPermission permission = permissionOptional.get();
                boolean update = false;
                if (StringUtils.hasText(dto.getName())) {
                    permission.setName(dto.getName());
                    update = true;
                }
                if (StringUtils.hasText(dto.getUrlPerm())) {
                    permission.setUrlPerm(dto.getUrlPerm());
                    update = true;
                }
                if (dto.getMenuId() != null) {
                    permission.setMenuId(dto.getMenuId());
                    update = true;
                }
                if (StringUtils.hasText(dto.getBtnPerm())) {
                    permission.setBtnPerm(dto.getBtnPerm());
                    update = true;
                }
                if (update) {
                    return Optional.of(permissionRepository.save(permission));
                }
            }
        }
        return Optional.empty();
    }


    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)){
            List<SysPermission> permissionList = permissionRepository.findAllById(idList);
            permissionRepository.deleteAll(permissionList);
        }
    }

    @Override
    public Optional<SysPermission> findById(Long id) {
        return permissionRepository.findById(id);
    }


    @Override
    public PermInfo entity2vo(SysPermission entity) {
        if (entity != null) {
            PermInfo permInfo = new PermInfo();
            permInfo.setPermId(entity.getPermId());
            permInfo.setName(entity.getName());
            permInfo.setMenuId(entity.getMenuId());
            permInfo.setUrlPerm(entity.getUrlPerm());
            permInfo.setBtnPerm(entity.getBtnPerm());
            return permInfo;
        }
        return null;
    }

    @Override
    public List<PermInfo> list2vo(List<SysPermission> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream().filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo).collect(Collectors.toList());
    }

    private List<PermissionDTO> allPermRoles() {
        List<SysPermission> permissionList = permissionRepository.findAll();
        List<Long> permIdList = permissionList.stream().map(SysPermission::getPermId).collect(Collectors.toList());
        List<SysRolePermission> rolePermissionList = rolePermissionRepository.findAllByPermIdIn(permIdList);
        List<Long> roleIdList = rolePermissionList.stream().map(SysRolePermission::getRoleId).collect(Collectors.toList());
        List<SysRole> roleList = roleService.findAllById(roleIdList);
        List<PermissionDTO> permissionDTOList = Lists.newArrayList();
        for (SysPermission sysPermission : permissionList) {
            PermissionDTO permission = new PermissionDTO();
            permission.setName(sysPermission.getName());
            permission.setMenuId(sysPermission.getMenuId());
            permission.setUrlPerm(sysPermission.getUrlPerm());
            permission.setBtnPerm(sysPermission.getBtnPerm());
            List<String> roleStrList = Lists.newArrayList();
            for (SysRolePermission sysRolePermission : rolePermissionList) {
                if (sysPermission.getPermId().equals(sysRolePermission.getPermId())) {
                    for (SysRole sysRole : roleList) {
                        if (sysRolePermission.getRoleId().equals(sysRole.getRoleId()) && StringUtils.hasText(sysRole.getName())) {
                            roleStrList.add(sysRole.getCode());
                        }
                    }
                }
            }
            permission.setRoles(roleStrList);
            permissionDTOList.add(permission);
        }

        return permissionDTOList;
    }
}
