package com.jymj.mall.order.repository;

import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.order.entity.MallOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 订单
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-14
 */
@Repository
public interface MallOrderRepository extends JpaRepository<MallOrder,Long>, JpaSpecificationExecutor<MallOrder> {
    Optional<MallOrder> findByOrderNo(String orderNo);

    Integer countByUserIdAndOrderStatus(Long userId, OrderStatusEnum orderStatus);

}
