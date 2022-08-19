package com.jymj.mall.admin.service;

import com.jymj.mall.admin.dto.AddDeptDTO;
import com.jymj.mall.admin.entity.SysDept;
import com.jymj.mall.admin.vo.DeptInfo;

import java.util.Optional;

/**
 * 部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
public interface DeptService {

    SysDept add(AddDeptDTO deptDTO);

    Optional<SysDept> findById(Long deptId);

    DeptInfo dept2vo(SysDept dept);
}
