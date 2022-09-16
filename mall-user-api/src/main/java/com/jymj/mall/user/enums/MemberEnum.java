package com.jymj.mall.user.enums;

import com.jymj.mall.common.enums.EnumTypeInfo;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 会员身份
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-16
 */
public enum MemberEnum {

    /**
     * 会员身份
     */
    ORDINARY_USER(0, "普通用户"),

    CARD_MEMBER(1, "卡会员"),

    MEMBER_V1(2, "会员LV1");


    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    private final Integer value;

    private final String label;


    MemberEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<EnumTypeInfo> toList() {
        List<EnumTypeInfo> typeInfoList = Lists.newArrayList();
        for (MemberEnum value : MemberEnum.values()) {

            EnumTypeInfo info = new EnumTypeInfo(value.name(), value.getLabel());

            typeInfoList.add(info);
        }
        return typeInfoList;
    }
}
