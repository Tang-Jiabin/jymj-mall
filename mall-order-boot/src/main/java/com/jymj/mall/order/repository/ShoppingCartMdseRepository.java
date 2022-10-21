package com.jymj.mall.order.repository;

import com.jymj.mall.order.entity.ShoppingCartMdse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 购物车
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
@Repository
public interface ShoppingCartMdseRepository extends JpaRepository<ShoppingCartMdse,Long>, JpaSpecificationExecutor<ShoppingCartMdse> {
    Optional<ShoppingCartMdse> findByUserIdAndMdseIdAndStockId(Long userId, Long mdseId, Long stockId);
}
