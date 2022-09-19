package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.BrandDTO;
import com.jymj.mall.mdse.entity.MdseBrand;
import com.jymj.mall.mdse.service.BrandService;
import com.jymj.mall.mdse.vo.BrandInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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

    private final BrandService brandService;

    @ApiOperation(value = "添加品牌")
    @PostMapping
    public Result<BrandInfo> addBrand(@Valid @RequestBody BrandDTO brandDTO) {
        MdseBrand brand = brandService.add(brandDTO);
        BrandInfo brandInfo = brandService.entity2vo(brand);
        return Result.success(brandInfo);
    }

    @ApiOperation(value = "删除品牌")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteBrand(@Valid @PathVariable String ids) {
        brandService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改品牌")
    @PutMapping
    public Result<BrandInfo> updateBrand(@RequestBody BrandDTO brandDTO) {
        Optional<MdseBrand> mdseBrandOptional = brandService.update(brandDTO);
        return mdseBrandOptional.map(entity -> Result.success(brandService.entity2vo(entity))).orElse(Result.failed("更新失败"));
    }

    @ApiOperation(value = "品牌信息")
    @GetMapping("/{brandId}/info")
    public Result<BrandInfo> getBrandById(@Valid @PathVariable Long brandId) {
        Optional<MdseBrand> mdseBrandOptional = brandService.findById(brandId);
        return mdseBrandOptional.map(entity -> Result.success(brandService.entity2vo(entity))).orElse(Result.failed("该品牌不存在"));
    }

    @ApiOperation(value = "品牌列表")
    @GetMapping("/lists")
    public Result<List<BrandInfo>> lists(Long mallId) {
        List<MdseBrand> brandList = brandService.findAllByMallId(mallId);
        List<BrandInfo> brandInfoList = brandService.list2vo(brandList);
        return Result.success(brandInfoList);
    }
}
