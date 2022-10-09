package com.jymj.mall.mdse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 分组
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@ApiModel("分组")
public class GroupInfo {

    @ApiModelProperty("分组id")
    private Long groupId;

    @ApiModelProperty("编码")
    private String number;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("显示")
    private Boolean show;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("商品数量")
    private Integer mdseCount;

}
