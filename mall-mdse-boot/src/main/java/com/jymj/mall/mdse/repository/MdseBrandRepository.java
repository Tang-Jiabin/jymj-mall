package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 品牌
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Repository
public interface MdseBrandRepository extends JpaRepository<MdseBrand,Long> {
    Optional<MdseBrand> findByName(String name);


    List<MdseBrand> findAllByShopIdIn(List<Long> shopIdList);
}
