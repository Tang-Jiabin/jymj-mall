package com.jymj.mall.marketing.dto;

import com.jymj.mall.common.enums.CouponStateEnum;
import com.jymj.mall.common.enums.CouponTypeEnum;
import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 优惠券分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("优惠券")
    private String couponName;

    @ApiModelProperty("商品分类id")
    private Long mdseCategoryId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("优惠券客户端状态 ")
    private CouponStateEnum state;

    @ApiModelProperty("优惠券管理端状态 1-启用 2-禁用")
    private Integer status;

    @ApiModelProperty("优惠券类型")
    private CouponTypeEnum type;

    @ApiModelProperty("生效时间")
    private Date effectiveTime;

    @ApiModelProperty("失效时间")
    private Date invalidTime;


}
