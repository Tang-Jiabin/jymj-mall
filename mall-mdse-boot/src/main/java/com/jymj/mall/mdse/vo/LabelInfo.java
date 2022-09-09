package com.jymj.mall.mdse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标签信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Data
@ApiModel("标签")
public class LabelInfo {
    @ApiModelProperty("标签id")
    private Long labelId;

    @ApiModelProperty("标签名称")
    private String name;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("商场id")
    private Long mallId;
}
