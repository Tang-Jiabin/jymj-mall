package com.jymj.mall.order.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 订单商品集合
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
@ApiModel("订单商品集合")
public class OrderMdseDTO {

    @ApiModelProperty("商品id")
    @NotNull(message = "商品id不能为空")
    private Long mdseId;

    @ApiModelProperty("库存id")
    @NotNull(message = "库存id不能为空")
    private Long stockId;

    @ApiModelProperty("店铺id")
    @NotNull(message = "店铺id不能为空")
    private Long shopId;

    @ApiModelProperty("购买数量")
    @NotNull(message = "购买数量不能为空")
    private Integer quantity;

    @ApiModelProperty("商品类型 1-商品 2-卡")
    private Integer type;
}
