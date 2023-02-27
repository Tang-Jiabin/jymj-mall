package com.jymj.mall.marketing.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.marketing.dto.CouponPageQuery;
import com.jymj.mall.marketing.dto.UserCouponDTO;
import com.jymj.mall.marketing.entity.UserCoupon;
import com.jymj.mall.marketing.vo.UserCouponInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 用户优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-23
 */
public interface UserCouponService extends BaseService<UserCoupon, UserCouponInfo, UserCouponDTO> {
    Page<UserCoupon> findPage(CouponPageQuery couponPageQuery);

    List<UserCoupon> findByIds(String ids);
}
