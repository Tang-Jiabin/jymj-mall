package com.jymj.mall.user.repository;

import com.jymj.mall.user.entity.MallUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-24
 */
@Repository
public interface MallUserRoleRepository extends JpaRepository<MallUserRole,Long> {
    List<MallUserRole> findAllByUserId(Long userId);
}
