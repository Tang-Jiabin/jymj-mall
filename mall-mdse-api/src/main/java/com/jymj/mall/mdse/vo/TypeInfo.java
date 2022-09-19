package com.jymj.mall.mdse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Data
@ApiModel("商品类型")
public class TypeInfo {

    @ApiModelProperty("类型id")
    private Long typeId;

    @ApiModelProperty("类型名称")
    private String name;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("商场id")
    private Long mallId;
}
