package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.LabelDTO;
import com.jymj.mall.mdse.entity.MdseLabel;
import com.jymj.mall.mdse.service.LabelService;
import com.jymj.mall.mdse.vo.LabelInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 商品标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Api(tags = "标签")
@RestController
@RequestMapping("/api/v1/label")
@RequiredArgsConstructor
public class LabelController {
    
    private final LabelService labelService;

    @ApiOperation(value = "添加标签")
    @PostMapping
    public Result<LabelInfo> addLabel(@Valid @RequestBody LabelDTO labelDTO) {
        MdseLabel label = labelService.add(labelDTO);
        LabelInfo labelInfo = labelService.entity2vo(label);
        return Result.success(labelInfo);
    }

    @ApiOperation(value = "删除标签")
    @DeleteMapping("/{ids}")
    public Result deleteLabel(@Valid @PathVariable String ids) {
        labelService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改标签")
    @PutMapping
    public Result<LabelInfo> updateLabel(@RequestBody LabelDTO labelDTO) {
        Optional<MdseLabel> mdseLabelOptional = labelService.update(labelDTO);
        return mdseLabelOptional.map(entity -> Result.success(labelService.entity2vo(entity))).orElse(Result.failed("更新失败"));
    }

    @ApiOperation(value = "标签信息")
    @GetMapping("/{labelId}/info")
    public Result<LabelInfo> getLabelById(@Valid @PathVariable Long labelId) {
        Optional<MdseLabel> mdseLabelOptional = labelService.findById(labelId);
        return mdseLabelOptional.map(entity -> Result.success(labelService.entity2vo(entity))).orElse(Result.failed("该标签不存在"));
    }

    @ApiOperation(value = "列表")
    @GetMapping("/lists")
    public Result<List<LabelInfo>> lists(Long mallId) {
        List<MdseLabel> labelList = labelService.findAllByMallId(mallId);
        List<LabelInfo> labelInfoList = labelService.list2vo(labelList);
        return Result.success(labelInfoList);
    }
    
}
