package com.jymj.mall.admin.repository;

import com.jymj.mall.admin.entity.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关联表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */

@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission,Long> {

    List<SysRolePermission> findAllByPermIdIn(List<Long> permIdList);
}
