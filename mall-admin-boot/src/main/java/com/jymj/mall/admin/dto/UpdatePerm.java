package com.jymj.mall.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改权限
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Data
@ApiModel("更新权限")
public class UpdatePerm {

    @ApiModelProperty("资源id")
    private Long permId;

    @NotBlank(message = "资源名称不能为空")
    @ApiModelProperty("资源名称")
    private String name;

    @ApiModelProperty("菜单id")
    private Long menuId;

    @NotBlank(message = "资源路径不能为空")
    @ApiModelProperty("资源路径")
    private String urlPerm;

    @ApiModelProperty("按钮路径")
    private String btnPerm;

}
