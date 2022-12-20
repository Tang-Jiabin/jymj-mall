package com.jymj.mall.shop.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RenovationInfo {

    @ApiModelProperty("id")
    private Long renovationId;

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

    @ApiModelProperty("子列表")
    private List<RenovationInfo> subEntry;
}
