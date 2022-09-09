package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdseMfg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 厂家
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Repository
public interface MdseMfgRepository extends JpaRepository<MdseMfg,Long> {
    Optional<MdseMfg> findByName(String name);


    List<MdseMfg> findAllByMallIdIn(List<Long> mallIdList);

    List<MdseMfg> findAllByMallId(Long mallId);
}
