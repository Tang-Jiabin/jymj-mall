package com.jymj.mall.shop.repository;

import com.jymj.mall.shop.entity.VerifyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 核销订单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-31
 */
@Repository
public interface VerifyOrderRepository extends JpaRepository<VerifyOrder,Long> {
}
