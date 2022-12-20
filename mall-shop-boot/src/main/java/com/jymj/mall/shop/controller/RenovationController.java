package com.jymj.mall.shop.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.shop.dto.RenovationDTO;
import com.jymj.mall.shop.entity.ShopRenovation;
import com.jymj.mall.shop.service.RenovationService;
import com.jymj.mall.shop.vo.RenovationInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 装修
 *
 *  * @author J.Tang
 *  * @version 1.0
 *  * @email seven_tjb@163.com
 *  * @date 2022-08-16
 */
@Api(tags = "商场装修")
@RestController
@RequestMapping("/api/v1/renovation")
@RequiredArgsConstructor
public class RenovationController {


    private final RenovationService renovationService;

    @ApiOperation(value = "添加装修")
    @PostMapping
    public Result<RenovationInfo> addRenovation(@Valid @RequestBody RenovationDTO renovationDTO) {
        ShopRenovation renovation = renovationService.add(renovationDTO);
        RenovationInfo renovationInfo = renovationService.entity2vo(renovation);
        return Result.success(renovationInfo);
    }

    @ApiOperation(value = "删除装修")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteRenovation(@Valid @PathVariable String ids) {
        renovationService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改装修")
    @PutMapping
    public Result<RenovationInfo> updateRenovation(@RequestBody RenovationDTO renovationDTO) {
        Optional<ShopRenovation> shopRenovationOptional = renovationService.update(renovationDTO);
        return shopRenovationOptional.map(entity -> Result.success(renovationService.entity2vo(entity))).orElse(Result.failed("更新失败"));
    }

    @ApiOperation(value = "装修信息")
    @GetMapping("/{renovationId}/info")
    public Result<RenovationInfo> getRenovationById(@Valid @PathVariable Long renovationId) {
        Optional<ShopRenovation> shopRenovationOptional = renovationService.findById(renovationId);
        return shopRenovationOptional.map(entity -> Result.success(renovationService.entity2vo(entity))).orElse(Result.failed("该装修不存在"));
    }

    @ApiOperation(value = "列表")
    @GetMapping("/lists")
    public Result<List<RenovationInfo>> lists(Long mallId) {
        List<ShopRenovation> renovationList = renovationService.findAllByMallId(mallId);
        List<RenovationInfo> renovationInfoList = renovationService.list2vo(renovationList);
        return Result.success(renovationInfoList);
    }
}
