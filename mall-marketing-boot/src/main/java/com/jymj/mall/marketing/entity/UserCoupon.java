package com.jymj.mall.marketing.entity;

import com.jymj.mall.common.enums.CouponStateEnum;
import com.jymj.mall.common.enums.CouponTypeEnum;
import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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
@Entity
@Table(name = "mall_user_coupon")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_user_coupon set deleted = 1 where coupon_id = ?")
@SQLDeleteAll(sql = "update mall_user_coupon set deleted = 1 where coupon_id in (?)")
public class UserCoupon extends BaseEntity {

    @Id
    @ApiModelProperty("优惠券ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_user_coupon_coupon_id_seq")
    @SequenceGenerator(name = "mall_user_coupon_coupon_id_seq",sequenceName = "mall_user_coupon_coupon_id_seq",allocationSize = 1)
    private Long couponId;

    @ApiModelProperty("商城ID")
    private Long mallId;

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
