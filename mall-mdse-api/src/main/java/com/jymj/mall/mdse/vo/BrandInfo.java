package com.jymj.mall.mdse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class BrandInfo {

    @ApiModelProperty("品牌id")
    private Long brandId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("logo")
    private String logo;

    @ApiModelProperty("别名")
    private String alias;

    @ApiModelProperty("备注")
    private String remarks;
}
