package com.jymj.mall.shop.repository;

import com.jymj.mall.shop.entity.ShopRenovation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RenovationRepository extends JpaRepository<ShopRenovation,Long>, JpaSpecificationExecutor<ShopRenovation> {
    List<ShopRenovation> findAllByMallId(Long mallId);

    List<ShopRenovation> findAllByParentId(Long parentId);

    List<ShopRenovation> findAllByMallIdAndStatus(Long mallId, Integer status);
}
