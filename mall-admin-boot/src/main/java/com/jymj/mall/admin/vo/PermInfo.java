package com.jymj.mall.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 权限vo
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Data
@ApiModel("资源信息")
public class PermInfo {

    @ApiModelProperty("资源id")
    private Long permId;

    @ApiModelProperty("资源名称")
    private String name;

    @ApiModelProperty("菜单id")
    private Long menuId;

    @ApiModelProperty("资源路径")
    private String urlPerm;

    @ApiModelProperty("按钮路径")
    private String btnPerm;
}
