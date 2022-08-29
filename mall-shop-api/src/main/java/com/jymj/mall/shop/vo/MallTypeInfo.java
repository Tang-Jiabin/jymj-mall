package com.jymj.mall.shop.vo;

import com.jymj.mall.shop.enums.MallType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 商场类型信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-26
 */
@AllArgsConstructor
@Getter
@Setter
public class MallTypeInfo {
    private  MallType value;

    private  String label;

}
