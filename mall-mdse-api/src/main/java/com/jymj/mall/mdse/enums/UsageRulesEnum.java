package com.jymj.mall.mdse.enums;

import com.jymj.mall.common.enums.EnumTypeInfo;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 使用规则
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */
public enum UsageRulesEnum {


    /**
     * 生效规则
     */
    PERMANENT(0,"长时间有效"),

    SAME_DAY(1,"当天有效"),

    WITHIN_FEW_DAYS(2,"几天内有效"),

    SECTION(3,"在时间内有效");


    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    private final Integer value;

    private final String label;


    UsageRulesEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<EnumTypeInfo> toList(){
        List<EnumTypeInfo> mallTypeInfoList = Lists.newArrayList();
        for (UsageRulesEnum value : UsageRulesEnum.values()) {

            EnumTypeInfo info = new EnumTypeInfo(value.name(),value.getLabel());

            mallTypeInfoList.add(info);
        }
        return mallTypeInfoList;
    }

}
