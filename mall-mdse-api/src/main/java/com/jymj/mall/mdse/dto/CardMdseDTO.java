package com.jymj.mall.mdse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 卡商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */
@Data
@ApiModel("卡商品")
public class CardMdseDTO {

    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("库存id")
    private Long stockId;

    @ApiModelProperty("数量")
    private Integer quantity;

}
