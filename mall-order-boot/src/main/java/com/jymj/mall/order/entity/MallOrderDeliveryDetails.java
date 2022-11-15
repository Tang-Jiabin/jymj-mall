package com.jymj.mall.order.entity;

import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import com.jymj.mall.common.web.pojo.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 订单配送详情
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
@Data
@Entity
@Table(name = "mall_order_delivery_details")
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_order_delivery_details SET deleted = 1 where order_delivery_details_id = ?")
@SQLDeleteAll(sql = "UPDATE mall_order_delivery_details SET deleted = 1 where order_delivery_details_id in (?)")
@EqualsAndHashCode(callSuper=true)
@EntityListeners({AuditingEntityListener.class})
public class MallOrderDeliveryDetails extends BaseEntity {

    @Id
    @ApiModelProperty("订单配送详情id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_order_delivery_details_id_seq")
    @SequenceGenerator(name = "mall_order_delivery_details_id_seq",sequenceName = "mall_order_delivery_details_id_seq",allocationSize = 1)
    private Long orderDeliveryDetailsId;

    @ApiModelProperty("配送方式")
    private OrderDeliveryMethodEnum orderDeliveryMethod;

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("地址id")
    private Long addressId;

    @ApiModelProperty("收件人")
    private String addressee;

    @ApiModelProperty("电话")
    private String mobile;

    @ApiModelProperty("详细地址")
    private String detailedAddress;


}
