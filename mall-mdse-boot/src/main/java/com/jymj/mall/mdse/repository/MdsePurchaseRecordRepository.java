package com.jymj.mall.mdse.repository;

import com.jymj.mall.mdse.entity.MdsePurchaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 购买记录
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-09
 */
@Repository
public interface MdsePurchaseRecordRepository extends JpaRepository<MdsePurchaseRecord,Long> , JpaSpecificationExecutor<MdsePurchaseRecord> {
    List<MdsePurchaseRecord> findAllByUserId(Long userId);

    List<MdsePurchaseRecord> findAllByUserIdAndType(Long userId, Long typeId);

    List<MdsePurchaseRecord> findAllByMdseId(Long mdseId);

    List<MdsePurchaseRecord> findAllByType(Integer type);
}
