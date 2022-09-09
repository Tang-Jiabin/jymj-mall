package com.jymj.mall.common.enums;

import lombok.Getter;

/**
 * 菜单枚举
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */

public enum MenuTypeEnum implements BaseEnum<Integer> {
    /**
     * 菜单类型
     */
    NULL(0, null),
    MENU(1, "菜单"),
    CATALOG(2, "目录"),
    EXT_LINK(3, "外链");

    @Getter
    private Integer value;

    @Getter
    private String label;

    MenuTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

}
