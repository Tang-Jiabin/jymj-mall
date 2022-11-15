package com.jymj.mall.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 会员信息
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
public class MemberInfo {

    @ApiModelProperty("id")
    private Long memberId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("身份证号")
    private String idNumber;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "会员等级")
    private Integer memberLevel;

}
