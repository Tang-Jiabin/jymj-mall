package com.jymj.mall.mdse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 规格信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Data
@ApiModel("规格信息")
public class SpecInfo {

    @ApiModelProperty("规格Id")
    private Long specId;

    @ApiModelProperty("规格名称")
    private String key;

    @ApiModelProperty("规格值")
    private String value;
}
