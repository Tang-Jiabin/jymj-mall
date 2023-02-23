package com.jymj.mall.common.enums;

import lombok.Getter;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 优惠券状态枚举
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-22
 */
public enum CouponStateEnum implements BaseEnum<Integer>{

    /**
     * 优惠券状态
     */
    NORMAL(1, "正常"),
    INEFFECTIVE(2, "未生效"),
    EXPIRED(3, "已过期"),
    USED(4, "已使用");


    @Getter
    private final Integer value;

    @Getter
    private final String label;

    CouponStateEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<EnumTypeInfo> toList() {
        List<EnumTypeInfo> typeInfoList = Lists.newArrayList();
        for (CouponStateEnum value : CouponStateEnum.values()) {

            EnumTypeInfo info = new EnumTypeInfo(value.name(), value.getLabel());

            typeInfoList.add(info);
        }
        return typeInfoList;
    }
}