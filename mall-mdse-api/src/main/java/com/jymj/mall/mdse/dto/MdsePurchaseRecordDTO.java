package com.jymj.mall.mdse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购买记录
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MdsePurchaseRecordDTO {

    private Long orderId;

    private Long userId;

    private Long mdseId;

    private Integer type;
}
