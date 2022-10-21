package com.jymj.mall.user.enums;

import com.jymj.mall.common.enums.BaseEnum;
import com.jymj.mall.common.enums.EnumTypeInfo;
import lombok.Getter;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 来源
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-16
 */
public enum SourceEnum implements BaseEnum<Integer>{
    /**
     * 用户来源
     */
    WEB(0, "网页端"),

    WECHAT(1, "微信小程序"),

    APP(2, "APP");


    @Getter
    private final Integer value;

    @Getter
    private final String label;


    SourceEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<EnumTypeInfo> toList() {
        List<EnumTypeInfo> typeInfoList = Lists.newArrayList();
        for (SourceEnum value : SourceEnum.values()) {

            EnumTypeInfo info = new EnumTypeInfo(value.name(), value.getLabel());

            typeInfoList.add(info);
        }
        return typeInfoList;
    }
}
