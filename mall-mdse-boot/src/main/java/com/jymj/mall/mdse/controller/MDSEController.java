package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.entity.MallMdse;
import com.jymj.mall.mdse.service.MdseService;
import com.jymj.mall.mdse.vo.MdseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
public class MDSEController {

    private final MdseService mdseService;

    @ApiOperation(value = "添加商品")
    @PostMapping
    private Result<MdseInfo> addMdse(@Valid @RequestBody MdseDTO mdseDTO) {
        MallMdse mallMdse = mdseService.add(mdseDTO);
        MdseInfo mdseInfo = mdseService.entity2vo(mallMdse);
        return Result.success(mdseInfo);
    }

    @ApiOperation(value = "删除商品")
    @DeleteMapping("/{ids}")
    private Result deleteMdse(@Valid @PathVariable String ids) {
        mdseService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改商品")
    @PutMapping
    private Result<MdseInfo> updateMdse(@RequestBody MdseDTO mdseDTO) {
        Optional<MallMdse> mdseOptional = mdseService.update(mdseDTO);
        return mdseOptional.map(entity -> Result.success(mdseService.entity2vo(entity))).orElse(Result.failed("修改失败"));
    }

    @ApiOperation(value = "商品信息")
    @GetMapping("/{mdseId}/info")
    public Result<MdseInfo> getMdseById(@Valid @PathVariable Long mdseId) {
        Optional<MallMdse> mdseOptional = mdseService.findById(mdseId);
        if (mdseOptional.isPresent()) {
            MallMdse mallMdse = mdseOptional.get();
            MdseInfo mdseInfo = mdseService.entity2vo(mallMdse);
            mdseInfo = mdseService.voAddGroupList(mdseInfo);
            mdseInfo = mdseService.voAddStockList(mdseInfo);
            mdseInfo = mdseService.voAddLabelList(mdseInfo);
            mdseInfo = mdseService.voAddPictureList(mdseInfo);
            mdseInfo = mdseService.voAddMfg(mdseInfo,mallMdse.getMfgId());
            mdseInfo = mdseService.voAddType(mdseInfo,mallMdse.getTypeId());
            mdseInfo = mdseService.voAddBrand(mdseInfo,mallMdse.getBrandId());

            return Result.success(mdseInfo);
        }

        return Result.failed("商品不存在");
    }

    @ApiOperation(value = "商品分页")
    @GetMapping("/pages")
    public Result<PageVO<MdseInfo>> pages(MdsePageQuery mdsePageQuery) {
        Page<MallMdse> page = mdseService.findPage(mdsePageQuery);
        List<MdseInfo> mdseInfoList = mdseService.list2vo(page.getContent());
        PageVO<MdseInfo> pageVo = PageUtils.toPageVO(page, mdseInfoList);
        return Result.success(pageVo);
    }
}
