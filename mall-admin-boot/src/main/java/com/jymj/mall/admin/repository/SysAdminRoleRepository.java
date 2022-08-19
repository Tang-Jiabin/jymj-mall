package com.jymj.mall.admin.repository;

import com.jymj.mall.admin.entity.SysAdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 管理员角色关联表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Repository
public interface SysAdminRoleRepository extends JpaRepository<SysAdminRole,Long> {


    List<SysAdminRole> findAllByAdminId(Long adminId);
}
