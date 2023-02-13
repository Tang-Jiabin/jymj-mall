package com.jymj.mall.user.repository;


import com.jymj.mall.user.entity.MallUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Repository
public interface UserRepository extends JpaRepository<MallUser,Long>, JpaSpecificationExecutor<MallUser> {

    Optional<MallUser> findByOpenid(String openid);

    Optional<MallUser> findByUsername(String username);
}
