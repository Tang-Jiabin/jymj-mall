package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MallMdseGroupMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分组关联表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Repository
public interface MdseGroupMapRepository extends JpaRepository<MallMdseGroupMap,Long> {
    List<MallMdseGroupMap> findAllByMdseId(Long mdseId);

    List<MallMdseGroupMap> findAllByGroupId(Long groupId);

    Integer countByGroupId(Long groupId);
}
