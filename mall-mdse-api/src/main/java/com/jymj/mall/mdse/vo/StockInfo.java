package com.jymj.mall.mdse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Data
@ApiModel("库存信息")
public class StockInfo {

    @ApiModelProperty("规格ID")
    private Long stockId;

    @ApiModelProperty("规格A")
    private SpecInfo specA;

    @ApiModelProperty("规格B")
    private SpecInfo specB;

    @ApiModelProperty("规格C")
    private SpecInfo specC;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("总库存")
    private Integer totalInventory;

    @ApiModelProperty("剩余库存")
    private Integer remainingStock;

    @ApiModelProperty("库存编号")
    private String number;

    @ApiModelProperty("库存图片")
    private List<PictureInfo> pictureList;


}
