package com.jymj.mall.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 会员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("会员信息")
public class MemberDTO {

    @ApiModelProperty("会员id")
    private Long memberId;

    @NotNull(message = "姓名不能为空")
    @ApiModelProperty("姓名")
    private String name;

    @NotNull(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobile;

    @NotNull(message = "详细地址不能为空")
    @ApiModelProperty("详细地址")
    private String address;

    @NotNull(message = "身份证号不能为空")
    @ApiModelProperty("身份证号")
    private String idNumber;

    @NotNull(message = "邮箱不能为空")
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "会员等级")
    private Integer level;
}
