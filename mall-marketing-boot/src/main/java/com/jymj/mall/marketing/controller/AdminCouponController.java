package com.jymj.mall.marketing.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.marketing.dto.CouponDTO;
import com.jymj.mall.marketing.dto.CouponPageQuery;
import com.jymj.mall.marketing.dto.UserCouponDTO;
import com.jymj.mall.marketing.entity.MallCoupon;
import com.jymj.mall.marketing.service.CouponService;
import com.jymj.mall.marketing.service.UserCouponService;
import com.jymj.mall.marketing.vo.CouponInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端优惠券
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023年2月22日
 */
@Api(tags = "管理端优惠券")
@RestController
@RequestMapping("/api/v1/admin/coupon")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @ApiOperation(value = "新增优惠券")
    @PostMapping
    public Result<Object> add(@RequestBody CouponDTO couponDTO) {
        couponService.add(couponDTO);
        return Result.success();
    }

    @ApiOperation(value = "删除优惠券")
    @DeleteMapping("/{ids}")
    public Result<Object> delete(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        couponService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改优惠券")
    @PutMapping
    public Result<Object> update(@RequestBody CouponDTO couponDTO) {
        couponService.update(couponDTO);
        return Result.success();
    }

    @ApiOperation(value = "优惠券分页")
    @GetMapping("/pages")
    public Result<PageVO<CouponInfo>> pages(CouponPageQuery couponPageQuery) {
        Page<MallCoupon> page = couponService.findPage(couponPageQuery);
        List<CouponInfo> couponInfoList = couponService.list2vo(page.getContent());
        PageVO<CouponInfo> pageVo = PageUtils.toPageVO(page, couponInfoList);
        return Result.success(pageVo);
    }

    @ApiOperation(value = "根据id查询优惠券")
    @GetMapping("/{id}/info")
    public Result<CouponInfo> get(@ApiParam("优惠券id") @PathVariable Long id) {
        return couponService.findById(id).map(coupon -> Result.success(couponService.entity2vo(coupon))).orElse(Result.failed("优惠券不存在"));
    }

    @ApiOperation(value = "新增用户优惠券")
    @PostMapping("/user")
    public Result<Object> addUserCoupon(@RequestBody UserCouponDTO userCouponDTO) {
        userCouponService.add(userCouponDTO);
        return Result.success();
    }

    @ApiOperation(value = "删除用户优惠券")
    @DeleteMapping("/user/{ids}")
    public Result<Object> deleteUserCoupon(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        userCouponService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改用户优惠券")
    @PutMapping("/user")
    public Result<Object> updateUserCoupon(@RequestBody UserCouponDTO userCouponDTO) {
        userCouponService.update(userCouponDTO);
        return Result.success();
    }

}
