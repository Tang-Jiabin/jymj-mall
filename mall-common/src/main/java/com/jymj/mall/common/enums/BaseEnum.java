package com.jymj.mall.common.enums;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
public interface BaseEnum<T> {


    T getValue();

    String getLabel();

    /**
     * 根据值获取枚举
     *
     */
    static <E extends Enum<E> & BaseEnum> E getEnumByValue(Object value, Class<E> clazz) {
        Objects.requireNonNull(value);
        EnumSet<E> allEnums = EnumSet.allOf(clazz); // 获取类型下的所有枚举

        E matchEnum = allEnums.stream()
                .filter(e -> Objects.equals(e.getValue(), value))
                .findFirst()
                .orElse(null);
        return matchEnum;
    }

    /**
     * 根据文本标签获取值
     *
     */
    static <E extends Enum<E> & BaseEnum> String getLabelByValue(Object value, Class<E> clazz) {
        Objects.requireNonNull(value);
        EnumSet<E> allEnums = EnumSet.allOf(clazz); // 获取类型下的所有枚举
        E matchEnum = allEnums.stream()
                .filter(e -> Objects.equals(e.getValue(), value))
                .findFirst()
                .orElse(null);

        String label = null;
        if (matchEnum != null) {
            label = matchEnum.getLabel();
        }
        return label;
    }


    /**
     * 根据文本标签获取值
     *
     */
    static <E extends Enum<E> & BaseEnum> Object getValueByLabel(String label, Class<E> clazz) {
        Objects.requireNonNull(label);
        EnumSet<E> allEnums = EnumSet.allOf(clazz); // 获取类型下的所有枚举
        String finalLabel = label;
        E matchEnum = allEnums.stream()
                .filter(e -> Objects.equals(e.getLabel(), finalLabel))
                .findFirst()
                .orElse(null);

        Object value = null;
        if (matchEnum != null) {
            value = matchEnum.getValue();
        }
        return value;
    }

}
