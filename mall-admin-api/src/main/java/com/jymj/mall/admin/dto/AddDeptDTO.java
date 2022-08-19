package com.jymj.mall.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 添加部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@Data
public class AddDeptDTO {

    @NotNull(message = "部门名称不能为空")
    @ApiModelProperty("部门名称")
    private String name;

    @NotNull(message = "父级id不能为空")
    @ApiModelProperty("父级id")
    private Long parentId;

    @ApiModelProperty("排序")
    private Integer sort;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty("状态")
    private Integer status;
}
