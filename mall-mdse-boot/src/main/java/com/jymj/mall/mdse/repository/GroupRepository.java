package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 分组
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Repository
public interface GroupRepository extends JpaRepository<MdseGroup,Long> , JpaSpecificationExecutor<MdseGroup> {
    Optional<MdseGroup> findByName(String name);

    List<MdseGroup> findAllByShopIdIn(List<Long> shopIdList);
}
