package com.jymj.mall.marketing.repository;

import com.jymj.mall.marketing.entity.MallCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-22
 */
@Repository
public interface CouponRepository extends JpaRepository<MallCoupon, Long>, JpaSpecificationExecutor<MallCoupon> {
}
