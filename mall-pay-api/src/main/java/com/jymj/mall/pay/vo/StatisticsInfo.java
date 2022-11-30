package com.jymj.mall.pay.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 统计信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsInfo {

    private String totalFee;

    private Long total;

    private String lastFee;

    private Date lastTime;
}
