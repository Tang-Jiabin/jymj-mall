package com.jymj.mall.shop.repository;

import com.jymj.mall.shop.entity.MallDetailsTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商场标签关联
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-25
 */
@Repository
public interface MallDetailsTagRepository extends JpaRepository<MallDetailsTag,Long> {
    List<MallDetailsTag> findAllByMallId(Long mallId);

    void deleteAllByMallIdAndTagIdIn(Long mallId, List<Long> deleteIdList);
}
