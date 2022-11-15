package com.jymj.mall.user.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPageQuery extends BasePageQueryDTO {
    @ApiModelProperty(value = "商品id")
    private Long mdseId;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "姓名")
    private String name;
}
