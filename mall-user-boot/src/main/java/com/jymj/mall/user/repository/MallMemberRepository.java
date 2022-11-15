package com.jymj.mall.user.repository;

import com.jymj.mall.user.entity.MallMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 会员信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-08
 */
@Repository
public interface MallMemberRepository extends JpaRepository<MallMember,Long>, JpaSpecificationExecutor<MallMember> {
    Optional<MallMember> findByUserId(Long userId);
}
