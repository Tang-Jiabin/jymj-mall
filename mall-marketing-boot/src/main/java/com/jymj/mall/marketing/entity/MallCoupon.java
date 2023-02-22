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
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_coupon_coupon_id_seq")
    @SequenceGenerator(name = "mall_coupon_coupon_id_seq",sequenceName = "mall_coupon_coupon_id_seq",allocationSize = 1)
    private Long couponId;

    @ApiModelProperty("优惠券名称")
    private String name;

    @ApiModelProperty("优惠券类型")
    private CouponTypeEnum type;

    @ApiModelProperty("优惠券金额")
    private BigDecimal amount;

    @ApiModelProperty("优惠券数量")
    private Integer quantity;

    @ApiModelProperty("优惠券使用开始时间")
    private Date startTime;

    @ApiModelProperty("优惠券使用结束时间")
    private Date endTime;

    @ApiModelProperty("优惠券使用说明")
    private String description;


}
