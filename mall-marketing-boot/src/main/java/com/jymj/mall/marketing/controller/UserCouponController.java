package com.jymj.mall.marketing.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.marketing.dto.CouponPageQuery;
import com.jymj.mall.marketing.entity.UserCoupon;
import com.jymj.mall.marketing.service.UserCouponService;
import com.jymj.mall.marketing.vo.UserCouponInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-23
 */
@Api(tags = "用户端优惠券")
@RestController
@RequestMapping("/api/v1/user/coupon")
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponService userCouponService;



    @ApiOperation(value = "优惠券分页")
    @GetMapping("/pages")
    public Result<PageVO<UserCouponInfo>> pages(CouponPageQuery couponPageQuery) {
        Long userId = UserUtils.getUserId();
        couponPageQuery.setUserId(userId);
        Page<UserCoupon> page = userCouponService.findPage(couponPageQuery);
        List<UserCouponInfo> couponInfoList = userCouponService.list2vo(page.getContent());
        PageVO<UserCouponInfo> pageVo = PageUtils.toPageVO(page, couponInfoList);
        return Result.success(pageVo);
    }

    @ApiOperation(value = "根据id查询优惠券")
    @GetMapping("/{id}/info")
    public Result<UserCouponInfo> get(@ApiParam("优惠券id") @PathVariable Long id) {
        return userCouponService.findById(id)
                .filter(coupon -> coupon.getUserId().equals(UserUtils.getUserId()))
                .map(coupon -> Result.success(userCouponService.entity2vo(coupon)))
                .orElse(Result.failed("优惠券不存在"));
    }

    @ApiOperation(value = "查询优惠券数量")
    @GetMapping("/number")
    public Result<Long> getNumber() {
        return Result.success(userCouponService.getNumber(UserUtils.getUserId()));
    }

}
