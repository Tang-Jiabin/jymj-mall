package com.jymj.mall.mdse.controller;

import java.lang.reflect.Array;
import java.math.BigDecimal;

import com.google.common.collect.Lists;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.redis.utils.RedisUtils;
import com.jymj.mall.mdse.dto.MdseInfoShow;
import com.jymj.mall.mdse.vo.MfgInfo;
import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import com.jymj.mall.mdse.vo.BrandInfo;
import com.jymj.mall.mdse.vo.TypeInfo;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.dto.MdseStatusDTO;
import com.jymj.mall.mdse.entity.MallMdse;
import com.jymj.mall.mdse.service.MdseService;
import com.jymj.mall.mdse.vo.MdseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Api(tags = "商品")
@RestController
@RequestMapping("/api/v1/mdse")
@RequiredArgsConstructor
public class MdseController {

    private final MdseService mdseService;



    @ApiOperation(value = "添加商品")
    @PostMapping
    public Result<MdseInfo> addMdse(@Valid @RequestBody MdseDTO mdseDTO) {
        MallMdse mallMdse = mdseService.add(mdseDTO);
        MdseInfo mdseInfo = mdseService.entity2vo(mallMdse);
        mdseService.syncToElasticAddMdseInfo(mdseInfo);
        return Result.success(mdseInfo);
    }

    @ApiOperation(value = "删除商品")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteMdse(@Valid @PathVariable String ids) {
        mdseService.delete(ids);
        Long[] lids = Arrays.stream(ids.split(",")).map(Long::parseLong).toArray(Long[]::new);
        mdseService.deleteCache(lids);
        mdseService.syncToElasticDeleteMdseInfo(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改商品")
    @PutMapping
    public Result<MdseInfo> updateMdse(@RequestBody MdseDTO mdseDTO) {
        Optional<MallMdse> mdseOptional = mdseService.update(mdseDTO);
        if (mdseOptional.isPresent()) {
            mdseService.deleteCache(mdseDTO.getMdseId());
            MdseInfo mdseInfo = mdseService.entity2vo(mdseOptional.get());
            mdseService.syncToElasticUpdateMdseInfo(mdseInfo);
            return Result.success(mdseInfo);
        }

        return Result.failed("修改失败");
    }

    @ApiOperation(value = "修改商品")
    @PutMapping("/status")
    public Result<MdseInfo> updateMdseStatus(@RequestBody MdseStatusDTO mdseDTO) {
        mdseService.updateStatus(mdseDTO);
        mdseService.deleteCache(mdseDTO.getMdseIds().toArray(new Long[0]));
        mdseService.syncToElasticUpdateMdseInfoList(mdseDTO.getMdseIds());
        return Result.success();
    }

    @ApiOperation(value = "商品信息")
    @GetMapping("/{mdseId}/info")
    public Result<MdseInfo> getMdseById(@Valid @PathVariable Long mdseId) {
        Optional<MallMdse> mdseOptional = mdseService.findById(mdseId);
        if (mdseOptional.isPresent()) {
            mdseService.deleteCache(mdseId);
            MallMdse mallMdse = mdseOptional.get();
            MdseInfo mdseInfo = mdseService.entity2vo(mallMdse);
            return Result.success(mdseInfo);
        }

        return Result.failed("商品不存在");
    }

    @ApiOperation(value = "商品分页")
    @GetMapping("/pages")
    public Result<PageVO<MdseInfo>> pages(MdsePageQuery mdsePageQuery) {
        Page<MallMdse> page = mdseService.findPage(mdsePageQuery);
        List<MdseInfo> mdseInfoList = mdseService.list2vo(page.getContent(), MdseInfoShow.builder().picture(true).build());
        PageVO<MdseInfo> pageVo = PageUtils.toPageVO(page, mdseInfoList);
        return Result.success(pageVo);
    }
}
