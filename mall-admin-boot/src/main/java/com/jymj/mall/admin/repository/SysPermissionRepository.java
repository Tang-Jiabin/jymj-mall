package com.jymj.mall.admin.repository;

import com.jymj.mall.admin.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-11
 */
@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission,Long> {
    Optional<SysPermission> findByUrlPerm(String urlPerm);
}
