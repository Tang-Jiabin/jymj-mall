package com.jymj.mall.order.vo;

import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单配送详情
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-17
 */
@Data
public class MallOrderDeliveryDetailsInfo {

    @ApiModelProperty("配送方式")
    private OrderDeliveryMethodEnum orderDeliveryMethod;

    @ApiModelProperty("地址id")
    private Long addressId;

    @ApiModelProperty("收件人")
    private String addressee;

    @ApiModelProperty("电话")
    private String mobile;

    @ApiModelProperty("详细地址")
    private String detailedAddress;
}
