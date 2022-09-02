package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.TypeDTO;
import com.jymj.mall.mdse.entity.MdseLabel;
import com.jymj.mall.mdse.entity.MdseType;
import com.jymj.mall.mdse.service.TypeService;
import com.jymj.mall.mdse.vo.LabelInfo;
import com.jymj.mall.mdse.vo.TypeInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 商品类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */

@Api(tags = "类型")
@RestController
@RequestMapping("/api/v1/type")
@RequiredArgsConstructor
public class TypeController {


    private final TypeService typeService;

    @ApiOperation(value = "添加类型")
    @PostMapping
    private Result<TypeInfo> addType(@Valid @RequestBody TypeDTO typeDTO) {
        MdseType type = typeService.add(typeDTO);
        TypeInfo typeInfo = typeService.entity2vo(type);
        return Result.success(typeInfo);
    }

    @ApiOperation(value = "删除类型")
    @DeleteMapping("/{ids}")
    private Result deleteType(@Valid @PathVariable String ids) {
        typeService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改类型")
    @PutMapping
    private Result<TypeInfo> updateType(@RequestBody TypeDTO typeDTO) {
        Optional<MdseType> mdseTypeOptional = typeService.update(typeDTO);
        return mdseTypeOptional.map(entity -> Result.success(typeService.entity2vo(entity))).orElse(Result.failed("更新失败"));
    }

    @ApiOperation(value = "类型信息")
    @GetMapping("/{typeId}/info")
    public Result<TypeInfo> getTypeById(@Valid @PathVariable Long typeId) {
        Optional<MdseType> mdseTypeOptional = typeService.findById(typeId);
        return mdseTypeOptional.map(entity -> Result.success(typeService.entity2vo(entity))).orElse(Result.failed("该类型不存在"));
    }

    @ApiOperation(value = "列表")
    @GetMapping("/lists")
    public Result<List<TypeInfo>> lists() {
        List<MdseType> typeList = typeService.findAllByAuth();
        List<TypeInfo> typeInfoList = typeService.list2vo(typeList);
        return Result.success(typeInfoList);
    }
}
