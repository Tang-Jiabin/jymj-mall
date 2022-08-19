package com.jymj.mall.shop.repository;


import com.jymj.mall.shop.entity.MallTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-16
 */
@Repository
public interface MallTagRepository extends JpaRepository<MallTag,Long> {
    List<MallTag> findAllByDeleted(int deleted);
}
