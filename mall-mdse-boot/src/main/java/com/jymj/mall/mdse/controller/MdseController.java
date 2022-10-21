package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdseInfoShow;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.dto.MdseStatusDTO;
import com.jymj.mall.mdse.entity.MallMdse;
import com.jymj.mall.mdse.service.MdseService;
import com.jymj.mall.mdse.vo.MdseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    @ApiOperation(value = "修改商品状态")
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

    @ApiOperation(value = "商品信息")
    @GetMapping("/{mdseId}/optional")
    public Result<MdseInfo> getMdseOptionalById(@Valid @PathVariable Long mdseId, MdseInfoShow show) {
        Optional<MallMdse> mdseOptional = mdseService.findById(mdseId);
        if (mdseOptional.isPresent()) {
            MallMdse mallMdse = mdseOptional.get();
            MdseInfo mdseInfo = mdseService.entity2vo(mallMdse, show);
            return Result.success(mdseInfo);
        }
        return Result.failed("商品不存在");
    }

    @GetMapping("/all/optional/{ids}")
    public Result<List<MdseInfo>> getAllMdseOptionalByIds(@Valid @PathVariable String ids, @SpringQueryMap MdseInfoShow show) {
        List<Long> lids = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<MallMdse> mallMdseList = mdseService.findAllById(lids);
        List<MdseInfo> mdseInfoList = mdseService.list2vo(mallMdseList, show);
        return Result.success(mdseInfoList);
    }

    @ApiOperation(value = "商品分页")
    @GetMapping("/pages")
    public Result<PageVO<MdseInfo>> pages(MdsePageQuery mdsePageQuery) {
        Page<MallMdse> page = mdseService.findPage(mdsePageQuery);
        List<MdseInfo> mdseInfoList = mdseService.list2vo(page.getContent(), MdseInfoShow.builder().picture(true).build());
        PageVO<MdseInfo> pageVo = PageUtils.toPageVO(page, mdseInfoList);
        return Result.success(pageVo);
    }


    @ApiOperation(value = "刷新商品到elastic")
    @PutMapping("/refreshToElastic")
    public Result<String> refreshToElastic() {
        List<MallMdse> mdseList = mdseService.findAll();
        List<MdseInfo> mdseInfoList = mdseService.list2vo(mdseList, MdseInfoShow.builder().group(true).stock(true).label(true).mfg(true).type(true).brand(true).shop(true).picture(true).build());
        mdseInfoList.forEach(mdseService::syncToElasticAddMdseInfo);
        return Result.success();
    }
}
