package com.jymj.mall.shop.controller;


import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.shop.dto.AddMallDTO;
import com.jymj.mall.shop.dto.MallPageQueryDTO;
import com.jymj.mall.shop.dto.UpdateMallDTO;
import com.jymj.mall.shop.entity.MallDetails;
import com.jymj.mall.shop.enums.MallType;
import com.jymj.mall.shop.service.MallService;
import com.jymj.mall.shop.vo.MallInfo;
import com.jymj.mall.shop.vo.MallTypeInfo;
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
 * 商场
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Api(tags = "商场")
@RestController
@RequestMapping("/api/v1/mall")
@RequiredArgsConstructor
public class MallController {

    private final MallService mallService;

    @ApiOperation(value = "添加商场")
    @PostMapping
    public Result addMall(@Valid  @RequestBody AddMallDTO mallDTO) {
        mallService.addMall(mallDTO);
        return Result.success();
    }

    @ApiOperation(value = "删除商场")
    @DeleteMapping("/{ids}")
    public Result deleteMall(@ApiParam("删除商场，多个以英文逗号(,)分割") @Valid @PathVariable String ids) {
        mallService.deleteMall(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改商场")
    @PutMapping
    public Result updateMall(@Valid @RequestBody UpdateMallDTO updateMallDTO) {
        mallService.updateMall(updateMallDTO);
        return Result.success();
    }

    @ApiOperation(value = "商场分页")
    @GetMapping("/pages")
    public Result<PageVO<MallInfo>> page(@Valid MallPageQueryDTO mallPageQuery){
        Page<MallDetails> page = mallService.findPage(mallPageQuery);
        List<MallInfo> mallVOList = mallService.list2vo(page.getContent());
        PageVO<MallInfo> pageVo = PageUtils.toPageVO(page, mallVOList);
        return Result.success(pageVo);
    }

    @ApiOperation(value = "商场信息")
    @GetMapping("/{mallId}/info")
    public Result<MallInfo> getMallById(@PathVariable Long mallId) {
        Optional<MallDetails> mallOptional = mallService.findById(mallId);
        MallDetails mallDetails = mallOptional.orElseThrow(() -> new BusinessException("商场不存在"));
        MallInfo mallInfo = mallService.mall2vo(mallDetails);
        return Result.success(mallInfo);
    }

    @ApiIgnore
    @ApiOperation(value = "商场信息")
    @GetMapping("/dept/{deptIds}")
    public Result<List<MallInfo>> getMallByDeptIdIn(@PathVariable String deptIds) {
        List<MallDetails> mallDetailsList = mallService.findAllByDeptIdIn(deptIds);
        List<MallInfo> mallInfoList = mallService.list2vo(mallDetailsList);
        return Result.success(mallInfoList);
    }

    @ApiOperation(value = "商场类型")
    @GetMapping("/type")
    public Result<List<MallTypeInfo>> getMallType() {
        return Result.success(MallType.toList());
    }


}
