package com.jymj.mall.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 核销人员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-26
 */
@Data
@ApiModel("核销人员")
public class VerifyPersonDTO {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("店铺id")
    private List<Long> shopIdList;

    @ApiModelProperty("商品id")
    private List<Long> mdseIdList;
}
