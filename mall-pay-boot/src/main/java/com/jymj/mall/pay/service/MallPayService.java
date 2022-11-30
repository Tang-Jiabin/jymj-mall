package com.jymj.mall.pay.service;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.jymj.mall.pay.vo.StatisticsInfo;

/**
 * 微信支付
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-17
 */
public interface MallPayService {

    WxPayUnifiedOrderRequest createWeChatPayOrder(String ip ,String orderNo);

    void WeChatPayNotify(WxPayOrderNotifyResult notifyResult);

    WxPayRefundRequest refund(String orderNo);

    StatisticsInfo getStatisticsByUserId(Long userId);
}
