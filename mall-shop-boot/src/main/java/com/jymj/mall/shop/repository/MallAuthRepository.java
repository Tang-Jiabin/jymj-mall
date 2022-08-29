package com.jymj.mall.shop.repository;

import com.jymj.mall.shop.entity.MallAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 商场授权
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
@Repository
public interface MallAuthRepository extends JpaRepository<MallAuth,Long> {
    Optional<MallAuth> findByMallId(Long mallId);
}
