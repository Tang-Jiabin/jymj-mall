package com.jymj.mall.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 角色资源
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */

@Data
@ApiModel("角色资源")
public class RoleResourceFormDTO {

    @ApiModelProperty("菜单ID集合")
    private List<Long> menuIds;

    @ApiModelProperty("权限ID集合")
    private List<Long> permIds;

}
