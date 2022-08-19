package com.jymj.mall.admin.repository;

import com.jymj.mall.admin.entity.SysAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Repository
public interface SysAdminRepository extends JpaRepository<SysAdmin,Long> {

    Optional<SysAdmin> findByUsernameAndDeleted(String username, int isDelete);

    List<SysAdmin> findAllByUsernameOrMobile(String username, String mobile);

    Optional<SysAdmin> findByAdminIdAndDeleted(Long adminId, int deleted);
}
