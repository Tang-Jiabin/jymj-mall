package com.jymj.mall.mdse.enums;

import com.jymj.mall.common.enums.BaseEnum;
import com.jymj.mall.common.enums.EnumTypeInfo;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 生效规则枚举
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */

public enum EffectiveRulesEnum implements BaseEnum<Integer> {


    /**
     * 生效规则
     */
    IMMEDIATE(0,"立即生效"),

    NEXT_DAY(1,"次日生效"),

    HOURS_LATER(2,"几小时后生效");


    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    private final Integer value;

    private final String label;


    EffectiveRulesEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<EnumTypeInfo> toList(){
        List<EnumTypeInfo> mallTypeInfoList = Lists.newArrayList();
        for (EffectiveRulesEnum value : EffectiveRulesEnum.values()) {

            EnumTypeInfo info = new EnumTypeInfo(value.name(),value.getLabel());

            mallTypeInfoList.add(info);
        }
        return mallTypeInfoList;
    }

}
