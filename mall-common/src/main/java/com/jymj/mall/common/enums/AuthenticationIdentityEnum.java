package com.jymj.mall.common.enums;

/**
 * 授权
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */


public enum AuthenticationIdentityEnum implements BaseEnum<String>{


    /**
     * 用户名
     */
    USERNAME("username", "用户名"),

    /**
     * 手机号
     */
    MOBILE("mobile", "手机号"),

    /**
     * 开放式认证系统唯一身份标识
     */
    OPENID("openId", "开放式认证系统唯一身份标识");


    private String value;

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }


    private String label;

    AuthenticationIdentityEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

}
