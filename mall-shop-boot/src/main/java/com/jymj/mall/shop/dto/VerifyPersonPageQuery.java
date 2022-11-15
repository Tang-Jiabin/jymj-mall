package com.jymj.mall.shop.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 核销员分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VerifyPersonPageQuery extends BasePageQueryDTO {

    @ApiModelProperty("用户昵称")
    private String userName;

    @ApiModelProperty("管理员昵称")
    private String adminName;
}
