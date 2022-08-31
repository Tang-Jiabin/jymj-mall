package com.jymj.mall.admin.repository;


import com.jymj.mall.admin.entity.SysDept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部门表
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Repository
public interface SysDeptRepository extends JpaRepository<SysDept, Long> {


    List<SysDept> findAllByParentId(Long districtId);

    Optional<SysDept> findByDeptIdAndDeleted(Long deptId, Integer deleted);

    List<SysDept> findAllByTreePathLike(String treePath);
}
