package com.jymj.mall.mdse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Data
@ApiModel("标签")
public class LabelDTO {

    @ApiModelProperty("标签id")
    private Long labelId;

    @NotNull(message = "名称不能为空")
    @ApiModelProperty("标签名称")
    private String name;

    @ApiModelProperty("备注")
    private String remarks;

    @NotNull(message = "店铺(网点)不能为空")
    @ApiModelProperty("店铺(网点)id")
    private Long shopId;
}
