package com.jymj.mall.admin.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员分页查询
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("编号")
    private String number;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("角色Id")
    private Long roleId;

    @ApiModelProperty("商城Id")
    private Long mallId;

}
