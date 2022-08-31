package com.jymj.mall.mdse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存dto
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@ApiModel("库存dto")
public class StockDTO {

    @ApiModelProperty("库存ID")
    private Long stockId;

    @ApiModelProperty("规格A")
    private SpecDTO specA;

    @ApiModelProperty("规格B")
    private SpecDTO specB;

    @ApiModelProperty("规格C")
    private SpecDTO specC;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("总库存")
    private Integer totalInventory;

    @ApiModelProperty("剩余库存")
    private Integer remainingStock;

    @ApiModelProperty("库存编号")
    private String number;

    @ApiModelProperty("规格图片")
    private List<String> specPictureList;
}
