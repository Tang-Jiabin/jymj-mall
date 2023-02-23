package com.jymj.mall.marketing.entity;

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
 * 优惠券
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-22
 */
@Data
@Entity
@Table(name = "mall_coupon")
@Where(clause = "deleted = 0")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
@SQLDelete(sql = "update mall_coupon set deleted = 1 where coupon_id = ?")
@SQLDeleteAll(sql = "update mall_coupon set deleted = 1 where coupon_id in (?)")
public class MallCoupon extends BaseEntity {

    @Id
    @ApiModelProperty("优惠券ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_coupon_coupon_id_seq")
    @SequenceGenerator(name = "mall_coupon_coupon_id_seq",sequenceName = "mall_coupon_coupon_id_seq",allocationSize = 1)
    private Long couponId;

    @ApiModelProperty("优惠券名称")
    private String name;

    @ApiModelProperty("优惠券类型")
    private CouponTypeEnum type;

    @ApiModelProperty("满减金额")
    private BigDecimal fullAmount;

    @ApiModelProperty("优惠券金额")
    private BigDecimal amount;

    @ApiModelProperty("优惠券数量")
    private Integer quantity;

    @ApiModelProperty("生效类型 1-立即生效 2-指定时间生效 3-领取后N天生效")
    private Integer effectiveType;

    @ApiModelProperty("生效时间")
    private Date effectiveTime;

    @ApiModelProperty("生效天数")
    private Integer effectiveDays;

    @ApiModelProperty("失效类型 1-永久有效 2-指定时间失效 3-领取后N天失效")
    private Integer invalidType;

    @ApiModelProperty("失效时间")
    private Date invalidTime;

    @ApiModelProperty("失效天数")
    private Integer invalidDays;

    @ApiModelProperty("领取类型 1-新用户领取 2-老用户领取 3-新老用户都可领取")
    private Integer receiveType;

    @ApiModelProperty("领取次数 0-不限制 1-1次 2-2次 3-3次 4-4次 5-5次")
    private Integer receiveCount;

    @ApiModelProperty("可分享")
    private Boolean share;

    @ApiModelProperty("优惠券使用说明")
    private String description;

    @ApiModelProperty("优惠券状态 1-启用 2-禁用")
    private Integer status;

    @ApiModelProperty("优惠券图片")
    private String picUrl;

    @ApiModelProperty("优惠券排序")
    private Integer sort;

    @ApiModelProperty("优惠券备注")
    private String remark;

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
