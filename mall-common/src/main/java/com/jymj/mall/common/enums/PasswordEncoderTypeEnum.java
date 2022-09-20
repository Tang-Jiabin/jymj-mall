package com.jymj.mall.common.enums;

import lombok.Getter;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-09
 */
public enum PasswordEncoderTypeEnum {
    /**
     * 加密方式
     */

    BCRYPT("{bcrypt}", "BCRYPT加密"),
    NOOP("{noop}", "无加密明文");

    @Getter
    private final String prefix;

    PasswordEncoderTypeEnum(String prefix, String desc) {
        this.prefix = prefix;
    }

}
