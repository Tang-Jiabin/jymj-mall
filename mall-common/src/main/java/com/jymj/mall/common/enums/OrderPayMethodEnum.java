package com.jymj.mall.common.enums;

import lombok.Getter;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 订单支付方式枚举
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
public enum OrderPayMethodEnum implements BaseEnum<Integer>{


    /**
     * 订单支付方式
     */
    A_LI_PAY(0, "支付宝"),
    WEI_XIN_PAY(1, "微信"),
    OFFLINE_PAY (2, "线下");

    @Getter
    private final Integer value;

    @Getter
    private final String label;

    OrderPayMethodEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<EnumTypeInfo> toList() {
        List<EnumTypeInfo> typeInfoList = Lists.newArrayList();
        for (OrderPayMethodEnum value : OrderPayMethodEnum.values()) {

            EnumTypeInfo info = new EnumTypeInfo(value.name(), value.getLabel());

            typeInfoList.add(info);
        }
        return typeInfoList;
    }
}
