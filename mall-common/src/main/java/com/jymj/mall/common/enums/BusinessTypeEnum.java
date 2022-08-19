package com.jymj.mall.common.enums;


import lombok.Getter;

/**
 * 业务类型枚举

 */
public enum BusinessTypeEnum implements BaseEnum<Integer> {

    USER(100, "用户"),
    ADMIN(200, "管理"),
    ORDER(300, "订单");

    @Getter
    private Integer value;

    @Getter
    private String label;

    BusinessTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
