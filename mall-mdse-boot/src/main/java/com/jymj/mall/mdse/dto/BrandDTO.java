package com.jymj.mall.mdse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 品牌
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@ApiModel("品牌")
public class BrandDTO {

    @ApiModelProperty("品牌id")
    private Long brandId;

    @NotBlank(message = "名称不能为空")
    @ApiModelProperty("名称")
    private String name;

    @NotBlank(message = "logo不能为空")
    @ApiModelProperty("logo")
    private String logo;

    @ApiModelProperty("别名")
    private String alias;

    @ApiModelProperty("备注")
    private String remarks;

    @NotNull(message = "商场id不能为空")
    @ApiModelProperty("商场id")
    private Long mallId;
}
