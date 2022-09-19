package com.jymj.mall.mdse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



/**
 * 厂家
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@ApiModel("厂家")
public class MfgInfo {

    @ApiModelProperty("厂家id")
    private Long mfgId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("logo")
    private String logo;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("备注")
    private String remarks;
}
