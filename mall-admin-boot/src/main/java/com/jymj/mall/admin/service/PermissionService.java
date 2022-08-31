package com.jymj.mall.admin.service;

import com.jymj.mall.admin.dto.UpdatePerm;
import com.jymj.mall.admin.entity.SysPermission;
import com.jymj.mall.admin.vo.PermInfo;
import com.jymj.mall.common.web.service.BaseService;

import java.util.List;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-11
 */
public interface PermissionService extends BaseService<SysPermission,PermInfo, UpdatePerm> {
    boolean refreshPermRolesRules();

    List<SysPermission> findAllByAdminId(Long adminId);
}
