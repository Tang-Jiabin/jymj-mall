package com.jymj.mall.shop.repository;

import com.jymj.mall.shop.entity.MallShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 店铺（网点）
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Repository
public interface MallShopRepository extends JpaRepository<MallShop,Long> , JpaSpecificationExecutor<MallShop> {
    List<MallShop> findAllByDeptIdIn(List<Long> deptIdList);

    List<MallShop> findAllByMallId(Long mallId);
}
