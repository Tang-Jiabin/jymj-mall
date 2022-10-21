package com.jymj.mall.user.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户地址")
public class AddressInfo {

    @ApiModelProperty("地址id")
    private Long addressId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("行政区")
    private String region;

    @ApiModelProperty("详细地址")
    private String detailedAddress;

    @ApiModelProperty("标签")
    private String label;

    @ApiModelProperty("状态 1-默认")
    private Integer status;

}
