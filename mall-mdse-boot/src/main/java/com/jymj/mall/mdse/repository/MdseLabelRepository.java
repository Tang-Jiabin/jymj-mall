package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Repository
public interface MdseLabelRepository  extends JpaRepository<MdseLabel,Long> {

    List<MdseLabel> findAllByMallIdIn(List<Long> mallIdList);

    List<MdseLabel> findAllByMallId(Long mallId);
}
