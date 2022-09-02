package com.jymj.mall.mdse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
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

    @NotNull(message = "店铺（网点）id不能为空")
    @ApiModelProperty("店铺（网点）id")
    private Long shopId;
}
