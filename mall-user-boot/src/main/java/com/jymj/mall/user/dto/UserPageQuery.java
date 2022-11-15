package com.jymj.mall.user.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import com.jymj.mall.user.enums.MemberEnum;
import com.jymj.mall.user.enums.SourceEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-14
 */
@Data
@ApiModel(value = "用户分页")
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends BasePageQueryDTO {

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "用户身份")
    private MemberEnum memberType;

    @ApiModelProperty(value = "来源")
    private SourceEnum sourceType;

    @ApiModelProperty(value = "购买次数")
    private Integer startPurchaseCount;

    @ApiModelProperty(value = "购买次数")
    private Integer endPurchaseCount;

    @ApiModelProperty(value = "商品id")
    private Long mdseId;
}
