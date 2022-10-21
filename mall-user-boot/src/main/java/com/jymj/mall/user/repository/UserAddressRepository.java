package com.jymj.mall.user.repository;

import com.jymj.mall.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地址
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {
    List<UserAddress> findAllByUserId(Long userId);
}
