package com.jymj.mall.admin.repository;


import com.jymj.mall.admin.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole,Long> {


    List<SysRole> findAllByRoleIdInAndDeleted(List<Long> ids, Integer deleted);
}
