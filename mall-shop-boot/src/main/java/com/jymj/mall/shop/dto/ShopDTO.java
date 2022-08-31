package com.jymj.mall.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalTime;

/**
 * 店铺（网点）dto
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Data
@ApiModel("店铺（网点）")
public class ShopDTO {

    @ApiModelProperty("店铺（网点）ID")
    private Long shopId;

    @NotBlank(message = "店铺名称不能为空")
    @ApiModelProperty("店铺名称")
    private String name;

    @NotBlank(message = "详细地址不能为空")
    @ApiModelProperty("详细地址")
    private String address;

    @NotBlank(message = "负责人不能为空")
    @ApiModelProperty("负责人")
    private String director;

    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "手机号格式不正确")
    @NotBlank(message = "联系电话不能为空")
    @ApiModelProperty("联系电话")
    private String mobile;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty("状态")
    private Integer status;

    @NotNull(message = "营业状态不能为空")
    @ApiModelProperty("营业状态 1-营业中 2-休息")
    private Integer inBusiness;

    @NotNull(message = "营业开始时间不能为空")
    @ApiModelProperty("营业开始时间")
    private LocalTime businessStartTime;

    @NotNull(message = "营业结束时间不能为空")
    @ApiModelProperty("营业结束时间")
    private LocalTime businessEndTime;

    @NotNull(message = "经度不能为空")
    @ApiModelProperty("经度")
    private String longitude;

    @NotNull(message = "纬度不能为空")
    @ApiModelProperty("纬度")
    private String latitude;

    @NotNull(message = "商场id不能为空")
    @ApiModelProperty("商场id")
    private Long mallId;
}
