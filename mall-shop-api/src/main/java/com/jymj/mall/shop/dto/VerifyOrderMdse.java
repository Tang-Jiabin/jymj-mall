package com.jymj.mall.shop.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 核销订单商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-31
 */
@Data
public class VerifyOrderMdse {

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("店铺id")
    private Long shopId;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("库存id")
    private Long stockId;

    @ApiModelProperty("核销数量")
    private Integer quantity;

}
