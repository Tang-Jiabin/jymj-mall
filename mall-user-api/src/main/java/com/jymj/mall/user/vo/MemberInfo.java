package com.jymj.mall.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "会员等级")
    private Integer memberLevel;

    @ApiModelProperty(value = "会员等级")
    private Integer level;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("生日")
    private String birth;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "性别")
    private Integer gender;

    @ApiModelProperty(value = "城市代码")
    private String cityCode;

}
