package com.jymj.mall.shop.controller;


import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.shop.dto.AddMallDTO;
import com.jymj.mall.shop.dto.MallPageQueryDTO;
import com.jymj.mall.shop.entity.MallDetails;
import com.jymj.mall.shop.service.MallService;
import com.jymj.mall.shop.vo.MallVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

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
    private Result addMall(@Valid @RequestBody AddMallDTO mallDTO) {
        mallService.add(mallDTO);
        return Result.success();
    }

    @ApiOperation(value = "商场分页")
    @GetMapping("/pages")
    public Result<PageVO<MallVO>> page(@Valid MallPageQueryDTO mallPageQuery){
        Page<MallDetails> page = mallService.findPage(mallPageQuery);
        List<MallVO> mallVOList = mallService.list2vo(page.getContent());
        PageVO<MallVO> pageVo = PageUtils.toPageVO(page, mallVOList);
        return Result.success(pageVo);
    }


}
