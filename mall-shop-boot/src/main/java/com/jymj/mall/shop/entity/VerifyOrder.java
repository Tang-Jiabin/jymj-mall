package com.jymj.mall.shop.entity;

import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 核销订单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-31
 */
@Data
@Entity
@Table(name = "mall_verify_order")
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_verify_order SET deleted = 1 where id = ?")
@EqualsAndHashCode(callSuper = true)
@EntityListeners({AuditingEntityListener.class})
public class VerifyOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mall_verify_order_id_seq")
    @SequenceGenerator(name = "mall_verify_order_id_seq", sequenceName = "mall_verify_order_id_seq", allocationSize = 1)
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("店铺id")
    private Long shopId;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("库存id")
    private Long stockId;

}
