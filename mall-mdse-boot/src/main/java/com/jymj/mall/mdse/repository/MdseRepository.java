package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MallMdse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Repository
public interface MdseRepository extends JpaRepository<MallMdse,Long>, JpaSpecificationExecutor<MallMdse> {
    List<MallMdse> findAllByShopIdIn(List<Long> shopIds);
}
