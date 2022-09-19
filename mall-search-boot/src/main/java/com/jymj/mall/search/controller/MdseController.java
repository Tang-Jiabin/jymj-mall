package com.jymj.mall.search.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.vo.MdseInfo;
import com.jymj.mall.search.service.MdseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品搜索
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-19
 */
@Api(tags = "商品")
@RestController
@RequestMapping("/api/v1/mdse")
@RequiredArgsConstructor
public class MdseController {

    private final MdseService mdseService;

    @ApiOperation(value = "添加商品")
    @PostMapping
    public Result<MdseInfo> addMdse(@Valid @RequestBody MdseInfo mdseInfo) {
        mdseInfo = mdseService.add(mdseInfo);
        return Result.success(mdseInfo);
    }

    @ApiOperation(value = "删除商品")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteMdse(@Valid @PathVariable String ids) {
        mdseService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改商品")
    @PutMapping
    public Result<MdseInfo> updateMdse(@RequestBody MdseInfo mdseInfo) {
        mdseInfo = mdseService.update(mdseInfo);
        return Result.success(mdseInfo);
    }

    @ApiOperation(value = "商品信息")
    @GetMapping("/{mdseId}/info")
    public Result<MdseInfo> getMdseById(@Valid @PathVariable Long mdseId) {
        Optional<MdseInfo> mdseOptional = mdseService.findById(mdseId);
        return mdseOptional.map(Result::success).orElseGet(() -> Result.failed("商品不存在"));

    }

    @ApiOperation(value = "商品分页")
    @GetMapping("/pages")
    public Result<PageVO<MdseInfo>> pages(MdsePageQuery mdsePageQuery) {
        SearchPage<MdseInfo> page = mdseService.findPage(mdsePageQuery);
        List<MdseInfo> content = page.getContent().stream().map(SearchHit::getContent).collect(Collectors.toList());
        PageVO<MdseInfo> pageVo = PageUtils.toPageVO(page, content);
        return Result.success(pageVo);
    }
}
