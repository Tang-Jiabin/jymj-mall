package com.jymj.mall.shop.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.shop.dto.ShopDTO;
import com.jymj.mall.shop.dto.ShopPageQuery;
import com.jymj.mall.shop.entity.MallShop;
import com.jymj.mall.shop.service.ShopService;
import com.jymj.mall.shop.vo.ShopInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 店铺（网点）
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Api(tags = "店铺（网点）")
@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @ApiOperation(value = "添加店铺")
    @PostMapping
    private Result<ShopInfo> addShop(@Valid @RequestBody ShopDTO shopDTO) {
        MallShop mallShop = shopService.add(shopDTO);
        ShopInfo shopInfo = shopService.entity2vo(mallShop);
        return Result.success(shopInfo);
    }

    @ApiOperation(value = "删除店铺")
    @DeleteMapping("/{ids}")
    private Result deleteShop(@Valid @PathVariable String ids) {
        shopService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改店铺")
    @PutMapping
    private Result<ShopInfo> updateShop(@RequestBody ShopDTO shopDTO) {
        Optional<MallShop> mallShopOptional = shopService.update(shopDTO);
        return mallShopOptional.map(shop -> Result.success(shopService.entity2vo(shop))).orElse(Result.failed());
    }

    @ApiOperation(value = "店铺信息")
    @GetMapping("/{shopId}/info")
    public Result<ShopInfo> getShopById(@Valid @PathVariable Long shopId) {
        Optional<MallShop> mallShopOptional = shopService.findById(shopId);
        return mallShopOptional.map(shop -> Result.success(shopService.entity2vo(shop))).orElse(Result.failed("店铺不存在"));
    }

    @ApiOperation(value = "店铺分页")
    @GetMapping("/pages")
    public Result<PageVO<ShopInfo>> pages(ShopPageQuery shopPageQuery) {
        Page<MallShop> page = shopService.findPage(shopPageQuery);
        List<ShopInfo> shopInfoList = shopService.list2vo(page.getContent());
        PageVO<ShopInfo> pageVo = PageUtils.toPageVO(page, shopInfoList);
        return Result.success(pageVo);
    }

    @ApiOperation(value = "店铺列表")
    @GetMapping("/lists")
    public Result<List<ShopInfo>> lists() {
        Long deptId = UserUtils.getDeptId();
        List<MallShop> mallShopList = shopService.findAllByDeptId(deptId);
        List<ShopInfo> shopInfoList = shopService.list2vo(mallShopList);
        return Result.success(shopInfoList);
    }
}
