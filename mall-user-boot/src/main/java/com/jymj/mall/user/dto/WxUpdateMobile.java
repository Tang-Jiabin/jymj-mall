package com.jymj.mall.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信修改手机号
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-14
 */
@Data
public class WxUpdateMobile {

    @NotBlank(message = " sessionKey not null")
    private String sessionKey;
    private String signature;
    private String rawData;
    @NotBlank(message = " encryptedData not null")
    private String encryptedData;
    @NotBlank(message = " iv not null")
    private String iv;
}
