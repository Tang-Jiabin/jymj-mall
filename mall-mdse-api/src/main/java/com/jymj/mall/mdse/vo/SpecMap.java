package com.jymj.mall.mdse.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 规格集合
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecMap {

    private String key;

    private List<String> values;
}
