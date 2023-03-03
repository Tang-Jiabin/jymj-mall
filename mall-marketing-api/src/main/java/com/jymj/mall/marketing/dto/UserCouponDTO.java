package com.jymj.mall.marketing.dto;

import com.jymj.mall.common.enums.CouponStateEnum;
import com.jymj.mall.common.enums.CouponTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户优惠券")
public class UserCouponDTO {

    @ApiModelProperty("优惠券ID")
    private Long couponId;

    @ApiModelProperty("优惠券模板ID")
    private Long couponTemplateId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("优惠券名称")
    private String name;

    @ApiModelProperty("优惠券类型")
    private CouponTypeEnum type;

    @ApiModelProperty("优惠券状态")
    private CouponStateEnum status;

    @ApiModelProperty("满减金额")
    private BigDecimal fullAmount;

    @ApiModelProperty("优惠券金额")
    private BigDecimal amount;

    @ApiModelProperty("生效时间")
    private Date effectiveTime;

    @ApiModelProperty("失效时间")
    private Date invalidTime;

    @ApiModelProperty("可分享")
    private Boolean share;

    @ApiModelProperty("优惠券使用说明")
    private String description;

    @ApiModelProperty("优惠券图片")
    private String picUrl;

    @ApiModelProperty("适用商品类型 1-全部商品 2-指定商品 3-指定商品不可用 4-指定分类 5-指定分类不可用")
    private Integer productType;

    @ApiModelProperty("适用商品")
    private String productIds;

    @ApiModelProperty("不适用商品")
    private String notProductIds;

    @ApiModelProperty("适用商品分类")
    private String productCategoryIds;

    @ApiModelProperty("不适用商品分类")
    private String notProductCategoryIds;


}
