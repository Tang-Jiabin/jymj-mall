package com.jymj.mall.admin.controller;

import com.jymj.mall.admin.entity.SysDistrict;
import com.jymj.mall.admin.service.DistrictService;
import com.jymj.mall.admin.vo.DistrictInfo;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.vo.OptionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 行政区
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Api(tags = "行政区")
@RestController
@RequestMapping("/api/v1/district")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @ApiOperation(value = "行政区下拉列表")
    @GetMapping("/options")
    public Result<List<OptionVO>> listDistrictOptions() {
        List<OptionVO> list = districtService.listDistrictOptions();
        return Result.success(list);
    }

    @ApiOperation(value = "子级列表")
    @GetMapping("/{districtId}/children")
    public Result<List<DistrictInfo>> children(@PathVariable Long districtId) {
        List<SysDistrict> districtList = districtService.findChildren(districtId);
        List<DistrictInfo> districtInfoList = districtService.list2vo(districtList);
        return Result.success(districtInfoList);
    }

    @ApiOperation(value = "父级列表")
    @GetMapping("/{districtId}/parent")
    public Result<List<DistrictInfo>> parent(@PathVariable Long districtId){
        List<SysDistrict> districtList = districtService.findParent(districtId);
        List<DistrictInfo> districtInfoList = districtService.list2vo(districtList);
        return Result.success(districtInfoList);
    }

}
