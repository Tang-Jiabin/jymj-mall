package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.AddAdminDTO;
import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.admin.entity.SysAdmin;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.admin.service.AdminService;
import com.jymj.mall.common.result.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Api(tags = "管理员")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @ApiIgnore
    @GetMapping("/{username}")
    public Result<AdminAuthDTO> loadAdminByUsername(@PathVariable String username) {
        AdminAuthDTO adminAuthDTO = adminService.getAuthInfoByUsername(username);
        return Result.success(adminAuthDTO);
    }

    @ApiOperation(value = "添加管理员")
    @PostMapping
    public Result add(@Valid @RequestBody AddAdminDTO adminDTO) {
        adminService.add(adminDTO);
        return Result.success();
    }

    @ApiOperation(value = "管理员信息")
    @GetMapping("/{adminId}/info")
    public Result<AdminInfo> getAdminById(@PathVariable Long adminId) {
        Optional<SysAdmin> adminOptional = adminService.findById(adminId);
        SysAdmin admin = adminOptional.orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        AdminInfo adminInfo = adminService.admin2vo(admin);
        return Result.success(adminInfo);
    }

}
