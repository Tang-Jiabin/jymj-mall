package com.jymj.mall.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateDeptDTO extends AddDeptDTO{

    @ApiModelProperty("部门Id")
    private Long deptId;
}
