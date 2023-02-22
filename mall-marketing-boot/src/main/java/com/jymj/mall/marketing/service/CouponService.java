package com.jymj.mall.marketing.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.marketing.dto.CouponDTO;
import com.jymj.mall.marketing.dto.CouponPageQuery;
import com.jymj.mall.marketing.entity.MallCoupon;
import com.jymj.mall.marketing.vo.CouponInfo;
import org.springframework.data.domain.Page;

/**
 * 优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-22
 */
public interface CouponService extends BaseService<MallCoupon, CouponInfo, CouponDTO> {
    Page<MallCoupon> findPage(CouponPageQuery couponPageQuery);
}
