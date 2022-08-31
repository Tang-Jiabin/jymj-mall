package com.jymj.mall.admin.service;

import com.jymj.mall.admin.dto.UpdateDeptDTO;
import com.jymj.mall.admin.entity.SysDept;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.web.service.BaseService;

import java.util.List;

/**
 * 部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
public interface DeptService extends BaseService<SysDept,DeptInfo,UpdateDeptDTO> {

    List<SysDept> findChildren(Long deptId);
}
