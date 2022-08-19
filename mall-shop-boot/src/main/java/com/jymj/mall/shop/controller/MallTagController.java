package com.jymj.mall.shop.controller;


import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.vo.OptionVO;
import com.jymj.mall.shop.service.MallTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商场标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-16
 */
@Api(tags = "商场标签")
@RestController
@RequestMapping("/api/v1/tag")
@RequiredArgsConstructor
public class MallTagController {

    private final MallTagService mallTagService;

    @ApiOperation(value = "标签下拉列表")
    @GetMapping("/options")
    public Result<List<OptionVO>> listTagsOptions() {
        List<OptionVO> list = mallTagService.listTagsOptions();
        return Result.success(list);
    }

}
