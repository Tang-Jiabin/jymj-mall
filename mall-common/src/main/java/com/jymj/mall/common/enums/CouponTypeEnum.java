package com.jymj.mall.common.enums;

import lombok.Getter;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 优惠券类型枚举
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2023-02-22
 */
public enum CouponTypeEnum implements BaseEnum<Integer> {

    // 优惠券类型
    FULL_REDUCTION(1, "满减券"),
    DISCOUNT(2, "折扣券"),
    EXCHANGE(3, "兑换券"),
    CASH(4, "代金券"),
    FULL_GIFT(5, "满赠券"),
    FREE_SHIPPING(6, "免邮券"),
    OTHER(7, "其他券");

    @Getter
    private final Integer value;

    @Getter
    private final String label;

    CouponTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<EnumTypeInfo> toList() {
        List<EnumTypeInfo> typeInfoList = Lists.newArrayList();
        for (CouponTypeEnum value : CouponTypeEnum.values()) {

            EnumTypeInfo info = new EnumTypeInfo(value.name(), value.getLabel());

            typeInfoList.add(info);
        }
        return typeInfoList;
    }

}
