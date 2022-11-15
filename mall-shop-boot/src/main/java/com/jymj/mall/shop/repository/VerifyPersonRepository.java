package com.jymj.mall.shop.repository;

import com.jymj.mall.shop.entity.VerifyPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 核销人
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-27
 */
@Repository
public interface VerifyPersonRepository extends JpaRepository<VerifyPerson,Long> , JpaSpecificationExecutor<VerifyPerson> {
    Optional<VerifyPerson> findByUserId(Long userId);

    Optional<VerifyPerson> findByAdminId(Long adminId);

    List<VerifyPerson> findAllByAdminIdIn(List<Long> adminIds);
}
