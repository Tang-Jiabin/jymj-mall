package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MallMdseGroupMap;
import com.jymj.mall.mdse.entity.MallMdseLabelMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分组中间表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Repository
public interface MdseLabelMapRepository extends JpaRepository<MallMdseLabelMap,Long> {
    List<MallMdseLabelMap> findAllByMdseId(Long mdseId);

    List<MallMdseLabelMap> findAllByLabelId(Long labelId);
}
