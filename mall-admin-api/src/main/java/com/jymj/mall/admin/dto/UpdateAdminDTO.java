package com.jymj.mall.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改管理员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateAdminDTO extends AddAdminDTO{

    @ApiModelProperty("管理员Id")
    private Long adminId;
}
