package com.jymj.mall.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jymj.mall.common.enums.OrderPayMethodEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单支付成功
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-20
 */
@Data
public class OrderPaySuccess {

    @ApiModelProperty("订单id")
    public Long orderId;

    @ApiModelProperty("实付金额")
    private BigDecimal amountActuallyPaid;

    @ApiModelProperty("支付方式")
    private OrderPayMethodEnum orderPayMethod;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("支付时间")
    private Date payTime;
}
