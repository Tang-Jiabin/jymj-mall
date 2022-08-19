package com.jymj.mall.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 部门信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@Data
public class DeptInfo {

    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("父级id")
    private Long parentId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态")
    private Integer status;
}
