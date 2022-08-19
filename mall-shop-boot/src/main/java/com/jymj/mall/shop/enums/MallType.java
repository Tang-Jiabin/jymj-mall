package com.jymj.mall.shop.enums;

/**
 * 商场类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-17
 */
public enum MallType {

    ZI_YING(1,"自营"),

    SHOU_QUAN(2,"授权");


    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    private final Integer value;

    private final String label;


    MallType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
