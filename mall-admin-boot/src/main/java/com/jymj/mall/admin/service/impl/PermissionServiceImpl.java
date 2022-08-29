package com.jymj.mall.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.jymj.mall.admin.dto.PermissionDTO;
import com.jymj.mall.admin.entity.SysPermission;
import com.jymj.mall.admin.entity.SysRole;
import com.jymj.mall.admin.entity.SysRolePermission;
import com.jymj.mall.admin.repository.SysPermissionRepository;
import com.jymj.mall.admin.repository.SysRolePermissionRepository;
import com.jymj.mall.admin.repository.SysRoleRepository;
import com.jymj.mall.admin.service.PermissionService;
import com.jymj.mall.common.constants.GlobalConstants;
import com.jymj.mall.common.redis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final SysRoleRepository roleRepository;
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
                Map<String, List<String>> urlPermRoles = new HashMap<>();
                urlPermList.stream().forEach(item -> {
                    String perm = item.getUrlPerm();
                    urlPermRoles.put(perm, item.getRoles());
                });
                log.info("添加URL拦截规则 : {}",urlPermRoles);
                redisUtils.hset(GlobalConstants.URL_PERM_ROLES_KEY, urlPermRoles);
            }
            // 初始化URL【按钮->角色(集合)】规则
            List<PermissionDTO> btnPermList = permissions.stream()
                    .filter(item -> StrUtil.isNotBlank(item.getBtnPerm()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(btnPermList)) {
                Map<String, List<String>> btnPermRoles = MapUtil.newHashMap();
                btnPermList.stream().forEach(item -> {
                    String perm = item.getBtnPerm();
                    btnPermRoles.put(perm, item.getRoles());
                });
                log.info("添加BTN拦截规则 : {}",btnPermRoles);
                redisUtils.hset(GlobalConstants.URL_PERM_ROLES_KEY, btnPermRoles);
            }
        }
        return true;
    }

    private List<PermissionDTO> allPermRoles() {
        List<SysPermission> permissionList = permissionRepository.findAll();
        List<Long> permIdList = permissionList.stream().map(SysPermission::getPermId).collect(Collectors.toList());
        List<SysRolePermission> rolePermissionList = rolePermissionRepository.findAllByPermIdIn(permIdList);
        List<Long> roleIdList = rolePermissionList.stream().map(SysRolePermission::getRoleId).collect(Collectors.toList());
        List<SysRole> roleList = roleRepository.findAllById(roleIdList);
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
