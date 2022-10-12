package com.jymj.mall.order.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 购物车商品
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
@Data
@ApiModel(description = "购物车商品")
public class ShoppingCartMdseDTO {

    @ApiModelProperty("购物车id")
    private Long shoppingCartId;

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
}
