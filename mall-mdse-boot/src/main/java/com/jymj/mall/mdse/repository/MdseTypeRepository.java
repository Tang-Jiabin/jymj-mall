package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品类别
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Repository
public interface MdseTypeRepository extends JpaRepository<MdseType,Long> {

    List<MdseType> findAllByShopIdIn(List<Long> shopIdList);
}
