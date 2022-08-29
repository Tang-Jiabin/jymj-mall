package com.jymj.mall.shop.enums;

import com.jymj.mall.shop.vo.MallTypeInfo;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 商场类型
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-17
 */
public enum MallType {

    ZI_YING(0,"自营"),

    SHOU_QUAN(1,"授权");


    public Integer getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    private final Integer value;

    private final String label;


    MallType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public static List<MallTypeInfo> toList(){
        List<MallTypeInfo> mallTypeInfoList = Lists.newArrayList();
        for (MallType value : MallType.values()) {
            MallTypeInfo info = new MallTypeInfo(value,value.getLabel());

            mallTypeInfoList.add(info);
        }
        return mallTypeInfoList;
    }
}
