package com.jymj.mall.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */

@Data
@ApiModel("角色表单对象")
public class AddRole {



    @ApiModelProperty("角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @ApiModelProperty("角色编码")
    @NotBlank(message = "角色编码不能为空")
    private String code;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("角色状态(1-正常；0-停用)")
    @NotNull(message = "角色状态不能为空")
    private Integer status;
}
