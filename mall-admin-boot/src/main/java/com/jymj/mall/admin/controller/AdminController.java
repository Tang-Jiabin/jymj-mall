package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.dto.AdminAuthDTO;
import com.jymj.mall.admin.dto.AdminPageQuery;
import com.jymj.mall.admin.dto.UpdateAdminDTO;
import com.jymj.mall.admin.entity.SysAdmin;
import com.jymj.mall.admin.service.AdminService;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
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
    public Result<AdminInfo> addAdmin(@Valid @RequestBody UpdateAdminDTO updateAdminDTO) {
        SysAdmin admin = adminService.add(updateAdminDTO);
        AdminInfo adminInfo = adminService.entity2vo(admin);
        return Result.success(adminInfo);
    }

    @ApiOperation(value = "删除管理员")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteAdmin(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        adminService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改管理员")
    @PutMapping
    public Result<AdminInfo> updateAdmin(@RequestBody UpdateAdminDTO updateAdminDTO) {
        Optional<SysAdmin> adminOptional = adminService.update(updateAdminDTO);
        return adminOptional.map(admin -> Result.success(adminService.entity2vo(admin))).orElse(Result.failed());
    }

    @ApiOperation(value = "管理员信息")
    @GetMapping("/id/{adminId}/info")
    public Result<AdminInfo> getAdminById(@PathVariable Long adminId) {
        Optional<SysAdmin> adminOptional = adminService.findById(adminId);
//        SysAdmin admin = adminOptional.orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        if (!adminOptional.isPresent()) {
            return Result.failed(ResultCode.USER_NOT_EXIST);
        }

        AdminInfo adminInfo = adminService.entity2vo(adminOptional.get());
        return Result.success(adminInfo);
    }

    @ApiOperation(value = "管理员信息")
    @GetMapping("/mobile/{mobile}/info")
    public Result<AdminInfo> getAdminByMobile(@PathVariable String mobile) {
        Optional<SysAdmin> adminOptional = adminService.findByMobile(mobile);
        SysAdmin admin = adminOptional.orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        AdminInfo adminInfo = adminService.entity2vo(admin);
        return Result.success(adminInfo);
    }

    @ApiOperation(value = "管理员分页")
    @GetMapping("/pages")
    public Result<PageVO<AdminInfo>> pages(AdminPageQuery adminPageQuery) {
        Page<SysAdmin> adminPage = adminService.findPage(adminPageQuery);
        List<AdminInfo> adminInfoList = adminService.list2vo(adminPage.getContent());
        PageVO<AdminInfo> adminInfoPage = PageUtils.toPageVO(adminPage, adminInfoList);
        return Result.success(adminInfoPage);
    }


}
