package com.jymj.mall.order.dto;

import com.jymj.mall.common.enums.OrderDeliveryMethodEnum;
import com.jymj.mall.common.enums.OrderStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("订单")
public class OrderDTO {

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("订单状态")
    private OrderStatusEnum statusEnum;

    @ApiModelProperty("地址id")
    private Long addressId;

    @ApiModelProperty("优惠券id")
    private List<Long> couponIdList;

    @ApiModelProperty("促销活动id")
    private List<Long> promotionIdList;

    @NotNull(message = "商品不能为空")
    @ApiModelProperty("订单商品集合")
    private List<OrderMdseDTO> orderMdseList;

    @ApiModelProperty("配送方式")
    private OrderDeliveryMethodEnum orderDeliveryMethod;

    @ApiModelProperty("备注")
    private String remarks;

}
