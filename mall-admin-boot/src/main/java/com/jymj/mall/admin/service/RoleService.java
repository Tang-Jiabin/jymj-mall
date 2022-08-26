package com.jymj.mall.admin.service;

import com.jymj.mall.admin.dto.RoleResourceFormDTO;
import com.jymj.mall.admin.dto.RoleFormDTO;
import com.jymj.mall.admin.entity.SysRole;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.web.vo.OptionVO;

import java.util.List;

/**
 * 角色
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
public interface RoleService {
    List<OptionVO> listRoleOptions();

    boolean saveRole(RoleFormDTO roleForm);

    SysRole getById(Long roleId);

    boolean deleteRoles(String ids);

    boolean updateRoleStatus(Long roleId, Integer status);

    RoleResourceFormDTO getRoleResources(Long roleId);

    boolean updateRoleResource(Long roleId, RoleResourceFormDTO roleResourceForm);

    List<SysRole> findAllById(List<Long> ids);

    List<RoleInfo> list2vo(List<SysRole> roleList);

    List<SysRole> findAllByAdminId(Long adminId);

    void deleteAdminRole(Long adminId, List<SysRole> deleteRoleList);

    void addAdminRole(Long adminId, List<SysRole> addRoleList);
}
