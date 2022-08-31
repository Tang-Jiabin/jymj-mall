package com.jymj.mall.common.result;

import java.io.Serializable;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */

public enum ResultCode implements IResultCode, Serializable {

    SUCCESS("200", "OK"),

    USER_ERROR("400", "用户端错误"),

    USERNAME_OR_PASSWORD_ERROR("400101", "用户名或密码错误"),
    USER_LOGIN_ERROR("400102", "用户登录异常"),
    PASSWORD_ENTER_EXCEED_LIMIT("400103", "用户输入密码次数超限"),
    USER_NOT_EXIST("400104", "用户不存在"),
    USER_ACCOUNT_LOCKED("400105", "用户账户被冻结"),
    USER_ACCOUNT_INVALID("400106", "用户账户已作废"),
    CLIENT_AUTHENTICATION_FAILED("400110", "客户端认证失败"),
    TOKEN_INVALID_OR_EXPIRED("400120", "token无效或已过期"),
    TOKEN_ACCESS_FORBIDDEN("400121", "token已被禁止访问"),

    AUTHORIZED_ERROR("401", "访问权限异常"),
    ACCESS_UNAUTHORIZED("401", "访问未授权"),

    DEGRADATION("500100", "系统功能降级"),
    SYSTEM_EXECUTION_ERROR("500", "系统执行出错"),
    SYSTEM_EXECUTION_TIMEOUT("504", "系统执行超时"),

    PARAM_ERROR("400", "用户请求参数错误"),
    RESOURCE_NOT_FOUND("404", "请求资源不存在"),
    PARAM_IS_NULL("410", "请求必填参数为空"),

    BUSINESS_FAIL("600", "业务执行失败");

    private String code;

    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    ResultCode() {
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    public static ResultCode getValue(String code) {
        for (ResultCode value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return SYSTEM_EXECUTION_ERROR; // 默认系统执行错误
    }
}
