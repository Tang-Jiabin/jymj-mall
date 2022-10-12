package com.jymj.mall.order.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.order.dto.ShoppingCartMdseDTO;
import com.jymj.mall.order.dto.ShoppingCartPageQuery;
import com.jymj.mall.order.entity.ShoppingCartMdse;
import com.jymj.mall.order.service.ShoppingCartService;
import com.jymj.mall.order.vo.ShoppingCartMdseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 购物车
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
@Api(tags = "购物车")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @ApiOperation(value = "添加商品")
    @PostMapping
    public Result<String> addMdse(@Valid @RequestBody ShoppingCartMdseDTO mdseDTO) {
        shoppingCartService.add(mdseDTO);
        return Result.success();
    }

    @ApiOperation(value = "删除商品")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteMdse(@Valid @PathVariable String ids) {
        shoppingCartService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改商品")
    @PutMapping
    public Result<ShoppingCartMdseInfo> updateMdse(@RequestBody ShoppingCartMdseDTO mdseDTO) {
        Optional<ShoppingCartMdse> mdseOptional = shoppingCartService.update(mdseDTO);
        return mdseOptional.map(entity -> Result.success(shoppingCartService.entity2vo(entity))).orElse(Result.failed("修改失败"));
    }


    @ApiOperation(value = "购物车分页")
    @GetMapping("/pages")
    public Result<PageVO<ShoppingCartMdseInfo>> pages(ShoppingCartPageQuery shoppingCartPageQuery) {
        shoppingCartPageQuery.setUserId(UserUtils.getUserId());
        Page<ShoppingCartMdse> page = shoppingCartService.findPage(shoppingCartPageQuery);
        List<ShoppingCartMdseInfo> shoppingCartMdseInfoList = shoppingCartService.list2vo(page.getContent());
        PageVO<ShoppingCartMdseInfo> pageVo = PageUtils.toPageVO(page, shoppingCartMdseInfoList);
        return Result.success(pageVo);
    }
}
