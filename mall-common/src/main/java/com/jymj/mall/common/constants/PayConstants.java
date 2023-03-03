package com.jymj.mall.common.constants;

/**
 * 支付常量
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
public interface PayConstants {


    /**
     * 支付主题
     */
    String SUBJECT = "Light up 生活-商城订单";
    /**
     * 微信支付交易类型 JSAPI
     * JSAPI--公众号支付
     */
    String WECHAT_PAY_TRADE_TYPE_JSAPI = "JSAPI";
    /**
     * 微信支付交易类型 NATIVE
     * NATIVE--原生扫码支付
     */
    String WECHAT_PAY_TRADE_TYPE_NATIVE = "JSAPI";
    /**
     * 微信支付交易类型 APP
     * APP--app支付
     */
    String WECHAT_PAY_TRADE_TYPE_APP = "APP";

}
