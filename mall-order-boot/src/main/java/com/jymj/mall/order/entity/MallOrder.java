package com.jymj.mall.order.entity;

import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import com.jymj.mall.common.enums.OrderPayMethodEnum;
import com.jymj.mall.common.enums.OrderStatusEnum;
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
 * 订单
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
@Data
@Entity
@Table(name = "mall_order",indexes = {@Index(name = "order_user_id",columnList = "userId")})
@Where(clause = "deleted = 0")
@SQLDelete(sql = "UPDATE mall_order SET deleted = 1 where order_id = ?")
@SQLDeleteAll(sql = "UPDATE mall_order SET deleted = 1 where order_id in (?)")
@EqualsAndHashCode(callSuper=true)
@EntityListeners({AuditingEntityListener.class})
public class MallOrder extends BaseEntity {

    @Id
    @ApiModelProperty("订单id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_order_order_id_seq")
    @SequenceGenerator(name = "mall_order_order_id_seq",sequenceName = "mall_order_order_id_seq",allocationSize = 1)
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("订单状态")
    private OrderStatusEnum orderStatus;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("订单总额")
    private BigDecimal totalAmount;

    @ApiModelProperty("应付金额")
    private BigDecimal amountPayable;

    @ApiModelProperty("实付金额")
    private BigDecimal amountActuallyPaid;

    @ApiModelProperty("支付方式")
    private OrderPayMethodEnum orderPayMethod;

    @ApiModelProperty("支付时间")
    private Date payTime;

    @ApiModelProperty("发货时间")
    private Date deliveryTime;

    @ApiModelProperty("收货时间")
    private Date receivingTime;

    @ApiModelProperty("订单配送详情id")
    private Long orderDeliveryDetailsId;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("配送方式")
    private OrderDeliveryMethodEnum orderDeliveryMethod;

}
