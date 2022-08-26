package com.jymj.mall.shop.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标签信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-26
 */
@Data
public class TagInfo {

    @ApiModelProperty("标签id")
    private Long tagId;

    @ApiModelProperty("标签名称")
    private String name;
}
