package com.jymj.mall.mdse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("库存dto")
public class StockDTO {

    @ApiModelProperty("库存ID")
    private Long stockId;

    @ApiModelProperty("商品id")
    private Long mdseId;

    @NotNull(message = "规格不能为空")
    @ApiModelProperty("规格A")
    private SpecDTO specA;

    @ApiModelProperty("规格B")
    private SpecDTO specB;

    @ApiModelProperty("规格C")
    private SpecDTO specC;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("原价")
    private BigDecimal originalPrice;

    @ApiModelProperty("折扣")
    private String discount;

    @ApiModelProperty("总库存")
    private Integer totalInventory;

    @ApiModelProperty("库存编号")
    private String number;

    @ApiModelProperty("规格图片")
    private List<PictureDTO> specPictureList;

}
