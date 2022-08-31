package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.MfgDTO;
import com.jymj.mall.mdse.vo.MfgInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 厂家
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Api(tags = "厂家")
@RestController
@RequestMapping("/api/v1/mfg")
@RequiredArgsConstructor
public class MFGController {



    @ApiOperation(value = "添加厂家")
    @PostMapping
    private Result<MfgInfo> addMfg(@Valid @RequestBody MfgDTO mfgDTO) {

        return Result.success();
    }

    @ApiOperation(value = "删除厂家")
    @DeleteMapping("/{ids}")
    private Result deleteMfg(@Valid @PathVariable String ids) {

        return Result.success();
    }

    @ApiOperation(value = "修改厂家")
    @PutMapping
    private Result<MfgInfo> updateMfg(@RequestBody MfgDTO mfgDTO) {

        return Result.success();
    }

    @ApiOperation(value = "厂家信息")
    @GetMapping("/{mfgId}/info")
    public Result<MfgInfo> getMfgById(@Valid @PathVariable Long mfgId) {

        return Result.success();
    }

    @ApiOperation(value = "厂家列表")
    @GetMapping("/lists")
    public Result<MfgInfo> lists() {

        return Result.success();
    }
}
