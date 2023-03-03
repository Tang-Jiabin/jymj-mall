package com.jymj.mall.marketing.repository;

import com.jymj.mall.common.enums.CouponStateEnum;
import com.jymj.mall.marketing.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-23
 */
@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, JpaSpecificationExecutor<UserCoupon> {
    List<UserCoupon> findByUserId(Long userId);

    List<UserCoupon> findAllByCouponTemplateId(Long couponId);

    Long countByUserId(Long userId);

    Long countByUserIdAndStatus(Long userId, CouponStateEnum normal);
}
