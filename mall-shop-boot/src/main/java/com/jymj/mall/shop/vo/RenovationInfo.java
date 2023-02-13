package com.jymj.mall.shop.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
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

    @ApiModelProperty("主页")
    private Integer homePage;

    @ApiModelProperty("子列表")
    private List<RenovationInfo> subEntry;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
