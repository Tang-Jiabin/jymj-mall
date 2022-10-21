package com.jymj.mall.order.vo;

import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import com.jymj.mall.common.enums.OrderPayMethodEnum;
import com.jymj.mall.common.enums.OrderStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 订单
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
@Data
public class MallOrderInfo {


    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("订单状态")
    private OrderStatusEnum orderStatus;

    @ApiModelProperty("订单总额")
    private BigDecimal totalAmount;

    @ApiModelProperty("应付金额")
    private BigDecimal amountPayable;

    @ApiModelProperty("实付金额")
    private BigDecimal amountActuallyPaid;

    @ApiModelProperty("支付方式")
    private OrderPayMethodEnum orderPayMethod;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;

    @ApiModelProperty("发货时间")
    private LocalDateTime deliveryTime;

    @ApiModelProperty("收货时间")
    private LocalDateTime receivingTime;

    @ApiModelProperty("订单商品信息")
    private List<MallOrderMdseDetailsInfo> orderMdseDetailsInfoList;

    @ApiModelProperty("订单配送信息")
    private MallOrderDeliveryDetailsInfo orderDeliveryDetailsInfo;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("配送方式")
    private OrderDeliveryMethodEnum orderDeliveryMethod;
}
