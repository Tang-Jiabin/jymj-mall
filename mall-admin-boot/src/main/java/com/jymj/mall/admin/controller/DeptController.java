package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.AddDeptDTO;
import com.jymj.mall.admin.entity.SysDept;
import com.jymj.mall.admin.service.DeptService;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@Api("部门")
@RequestMapping("/api/v1/dept")
@RestController
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @ApiOperation(value = "添加管理员")
    @PostMapping
    public Result<DeptInfo> add(@Valid @RequestBody AddDeptDTO deptDTO) {
        SysDept sysDept = deptService.add(deptDTO);
        return Result.success(deptService.dept2vo(sysDept));
    }
}
