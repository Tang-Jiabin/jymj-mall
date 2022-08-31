package com.jymj.mall.admin.service;

import com.jymj.mall.admin.dto.AddRole;
import com.jymj.mall.admin.dto.RolePageQuery;
import com.jymj.mall.admin.dto.RoleResource;
import com.jymj.mall.admin.dto.UpdateRole;
import com.jymj.mall.admin.entity.SysAdminRole;
import com.jymj.mall.admin.entity.SysRole;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.web.vo.OptionVO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * 角色
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
public interface RoleService {
    List<OptionVO<Long>> listRoleOptions();

    SysRole addRole(AddRole roleForm);

    Optional<SysRole> getById(Long roleId);

    void deleteRoles(String ids);

    boolean updateRoleStatus(Long roleId, Integer status);

    RoleResource getRoleResources(Long roleId);

    void updateRoleResource(Long roleId, RoleResource roleResourceForm);

    List<SysRole> findAllById(List<Long> ids);

    List<RoleInfo> list2vo(List<SysRole> roleList);

    List<SysRole> findAllByAdminId(Long adminId);

    void deleteAdminRole(Long adminId, List<SysRole> deleteRoleList);

    void addAdminRole(Long adminId, List<SysRole> addRoleList);

    SysRole updateRole(UpdateRole updateRole);

    Page<SysRole> findPage(RolePageQuery rolePageQuery);

    List<SysAdminRole> findAdminRoleAllByRoleId(Long roleId);
}
