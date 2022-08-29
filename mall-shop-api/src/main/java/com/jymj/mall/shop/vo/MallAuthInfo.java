package com.jymj.mall.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 商场授权信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "商场授权信息")
public class MallAuthInfo {

    @NotNull(message = "授权id不能为空")
    @ApiModelProperty("授权id")
    private Long authId;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("公司地址")
    private String companyAddress;

    @ApiModelProperty("法人")
    private String legalPerson;

    @ApiModelProperty("身份证")
    private String  identity;

    @ApiModelProperty("统一社会信用代码")
    private String unifiedSocialCreditCode;

    @ApiModelProperty("联系电话")
    private String mobile;

    @ApiModelProperty("营业执照")
    private String license;

    @ApiModelProperty("身份证（正面）")
    private String idFront;

    @ApiModelProperty("身份证（反面）")
    private String idBack;
}
