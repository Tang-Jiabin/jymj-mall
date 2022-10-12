package com.jymj.mall.order.vo;

import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import com.jymj.mall.mdse.vo.*;
import com.jymj.mall.shop.vo.ShopInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车商品VO
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-11
 */
@Data
@ApiModel("购物车商品信息")
public class ShoppingCartMdseInfo {

    @ApiModelProperty("购物车id")
    private Long shoppingCartId;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("商品图片")
    private PictureInfo pictureInfo;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品编号")
    private String number;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("起售数量")
    private Integer startingQuantity;

    @ApiModelProperty("店铺")
    private ShopInfo shopInfo;

    @ApiModelProperty("库存规格")
    private StockInfo stockInfo;

    @ApiModelProperty("商品状态 1-上架 2-下架")
    private Integer status;

    @ApiModelProperty("购买数量")
    private Integer quantity;

}
