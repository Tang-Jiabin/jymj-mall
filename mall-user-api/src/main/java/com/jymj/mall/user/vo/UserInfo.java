package com.jymj.mall.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jymj.mall.common.localdate.LocalDateDeserializer;
import com.jymj.mall.common.localdate.LocalDateSerializer;
import com.jymj.mall.user.enums.MemberEnum;
import com.jymj.mall.user.enums.SourceEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

/**
 * 用户
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-13
 */
@Data
public class UserInfo {

    @ApiModelProperty(value = "id" )
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "性别")
    private Integer gender;

    @ApiModelProperty(value = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @ApiModelProperty(value = "头像")
    private String avatarUrl;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "用户身份")
    private MemberEnum memberType;

    @ApiModelProperty(value = "来源")
    private SourceEnum sourceType;

    @ApiModelProperty(value = "购买次数")
    private Integer purchaseCount;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "登录时间")
    private Date loginTime;

    @ApiModelProperty(value = "核销人员 1-是 2-不是")
    private Integer verifyPerson;

    @ApiModelProperty(value = "会员等级")
    private Integer memberLevel;

    @ApiModelProperty("会员信息")
    private MemberInfo memberInfo;
}
