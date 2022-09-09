package com.jymj.mall.common.enums;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 枚举类型信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnumTypeInfo {

    private  String value;

    private  String label;
}
