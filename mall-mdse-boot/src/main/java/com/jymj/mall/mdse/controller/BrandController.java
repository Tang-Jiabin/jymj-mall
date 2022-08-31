package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.BrandDTO;
import com.jymj.mall.mdse.vo.BrandInfo;
import com.jymj.mall.mdse.vo.MfgInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 品牌
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Api(tags = "品牌")
@RestController
@RequestMapping("/api/v1/brand")
@RequiredArgsConstructor
public class BrandController {
    
    
    @ApiOperation(value = "添加品牌")
    @PostMapping
    private Result<BrandInfo> addBrand(@Valid @RequestBody BrandDTO brandDTO) {

        return Result.success();
    }

    @ApiOperation(value = "删除品牌")
    @DeleteMapping("/{ids}")
    private Result deleteBrand(@Valid @PathVariable String ids) {

        return Result.success();
    }

    @ApiOperation(value = "修改品牌")
    @PutMapping
    private Result<BrandInfo> updateBrand(@RequestBody BrandDTO brandDTO) {

        return Result.success();
    }

    @ApiOperation(value = "品牌信息")
    @GetMapping("/{brandId}/info")
    public Result<BrandInfo> getBrandById(@Valid @PathVariable Long brandId) {

        return Result.success();
    }

    @ApiOperation(value = "品牌列表")
    @GetMapping("/lists")
    public Result<MfgInfo> lists() {

        return Result.success();
    }
}
