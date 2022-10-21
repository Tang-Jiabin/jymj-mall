package com.jymj.mall.common.enums;

import lombok.Getter;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 订单状态
 *
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-13
 */
public enum OrderStatusEnum implements BaseEnum<Integer>{

    /**
     * 订单状态
     */
    UNPAID(0, "未付款"),
    UNSHIPPED(1, "未发货"),
    UNRECEIVED(2, "未收货"),
    COMPLETED(3, "已完成"),
    AFTER_SALES(4, "售后"),
    CANCELED(5, "取消"),
    CLOSED(6, "关闭");

    @Getter
    private final Integer value;

    @Getter
    private final String label;

    OrderStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<EnumTypeInfo> toList() {
        List<EnumTypeInfo> typeInfoList = Lists.newArrayList();
        for (OrderStatusEnum value : OrderStatusEnum.values()) {

            EnumTypeInfo info = new EnumTypeInfo(value.name(), value.getLabel());

            typeInfoList.add(info);
        }
        return typeInfoList;
    }
}
