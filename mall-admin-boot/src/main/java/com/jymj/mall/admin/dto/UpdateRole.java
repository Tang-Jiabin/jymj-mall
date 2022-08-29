package com.jymj.mall.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 修改角色
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
@Data
public class UpdateRole {


    @NotNull(message = "角色id不能为空")
    @ApiModelProperty("角色ID")
    private Long id;

    @ApiModelProperty("角色名称")
    private String name;

    @ApiModelProperty("角色编码")
    private String code;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("角色状态(1-正常；0-停用)")
    private Integer status;

}
