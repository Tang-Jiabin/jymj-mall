package com.jymj.mall.shop.dto;

import lombok.Data;

/**
 * 核销订单商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-31
 */
@Data
public class VerifyOrderMdse {

    private Long orderId;

    private Long shopId;

    private Long mdseId;

    private Long stockId;


}
