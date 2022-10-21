package com.jymj.mall.user.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 地址信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
@Data
@ApiModel("用户地址")
public class AddressDTO {

    @ApiModelProperty("地址id")
    private Long addressId;

    @NotNull(message = "姓名不能为空")
    @ApiModelProperty("姓名")
    private String name;

    @NotNull(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobile;

    @NotNull(message = "行政区不能为空")
    @ApiModelProperty("行政区")
    private String region;

    @NotNull(message = "详细地址不能为空")
    @ApiModelProperty("详细地址")
    private String detailedAddress;

    @ApiModelProperty("标签")
    private String label;

    @NotNull(message = "默认地址状态不能为空")
    @ApiModelProperty("状态 1-默认")
    private Integer status;

}
