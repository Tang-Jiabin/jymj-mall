package com.jymj.mall.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/**
 * 店铺（网点）
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Data
@ApiModel("店铺（网点）")
public class ShopInfo {

    @ApiModelProperty("店铺（网点）id")
    private Long shopId;

    @ApiModelProperty("店铺名称")
    private String name;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("负责人")
    private String director;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("营业状态 1-营业中 2-休息")
    private Integer inBusiness;


    @ApiModelProperty("营业开始时间")
    private String businessStartTime;


    @ApiModelProperty("营业结束时间")
    private String businessEndTime;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;


}
