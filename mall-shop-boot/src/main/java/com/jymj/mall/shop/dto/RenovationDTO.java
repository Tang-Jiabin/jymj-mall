package com.jymj.mall.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("店铺装修")
public class RenovationDTO {
    @ApiModelProperty("id")
    private Long renovationId;

    @ApiModelProperty("父id")
    private Long parentId;

    @ApiModelProperty("商场id")
    private Long mallId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("样式")
    private String style;

    @ApiModelProperty("数据")
    private String data;

    @ApiModelProperty("组件")
    private String components;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("主页")
    private Integer homePage;
}
