package com.jymj.mall.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 添加商场认证
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
@Data
@ApiModel(description = "添加商场认证")
public class AddMallAuth {

    @NotNull(message = "商场ID不能为空")
    @ApiModelProperty("商场ID")
    private Long mallId;

    @NotBlank(message = "公司名称不能为空")
    @ApiModelProperty("公司名称")
    private String companyName;

    @NotBlank(message = "公司地址不能为空")
    @ApiModelProperty("公司地址")
    private String companyAddress;

    @NotBlank(message = "法人不能为空")
    @ApiModelProperty("法人")
    private String legalPerson;

    @NotBlank(message = "身份证不能为空")
    @ApiModelProperty("身份证")
    private String  identity;

    @NotBlank(message = "统一社会信用代码不能为空")
    @ApiModelProperty("统一社会信用代码")
    private String unifiedSocialCreditCode;

    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "手机号格式不正确")
    @NotBlank(message = "联系电话不能为空")
    @ApiModelProperty("联系电话")
    private String mobile;

    @NotBlank(message = "营业执照不能为空")
    @ApiModelProperty("营业执照")
    private String license;

    @NotBlank(message = "身份证（正面）不能为空")
    @ApiModelProperty("身份证（正面）")
    private String idFront;

    @NotBlank(message = "身份证（反面不能为空")
    @ApiModelProperty("身份证（反面）")
    private String idBack;
}
