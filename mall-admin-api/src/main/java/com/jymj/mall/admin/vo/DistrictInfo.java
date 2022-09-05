package com.jymj.mall.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 行政区
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@Data
public class DistrictInfo {

    @ApiModelProperty("主键id")
    private Long districtId;

    @ApiModelProperty("父id")
    private Long pid;

    @ApiModelProperty("行政区名称")
    private String name;

    @ApiModelProperty("行政区代码")
    private String code;

    @ApiModelProperty("中心点")
    private String center;

    @ApiModelProperty("ids")
    private String[] ids;

}
