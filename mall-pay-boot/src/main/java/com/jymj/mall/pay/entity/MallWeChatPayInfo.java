package com.jymj.mall.pay.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 商城微信支付信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-20
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mall_wechat_pay_info")
public class MallWeChatPayInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_wechat_pay_info_pay_id_seq")
    @SequenceGenerator(name = "mall_wechat_pay_info_pay_id_seq",sequenceName = "mall_wechat_pay_info_pay_id_seq",allocationSize = 1)
    private Long payId;

    private Long orderId;
    /**
     * openid
     */
    private String openid;
    /**
     * isSubscribe
     */
    private String isSubscribe;
    /**
     * tradeType
     */
    private String tradeType;
    /**
     * bankType
     */
    private String bankType;
    /**
     * totalFee
     */
    private Integer totalFee;
    /**
     * feeType
     */
    private String feeType;
    /**
     * cashFee
     */
    private Integer cashFee;
    /**
     * transactionId
     */
    private String transactionId;
    /**
     * outTradeNo
     */
    private String outTradeNo;
    /**
     * timeEnd
     */
    private String timeEnd;
    /**
     * returnCode
     */
    private String returnCode;
    /**
     * resultCode
     */
    private String resultCode;
    /**
     * appid
     */
    private String appid;
    /**
     * mchId
     */
    private String mchId;
    /**
     * nonceStr
     */
    private String nonceStr;
    /**
     * sign
     */
    private String sign;
    /**
     * xmlString
     */
    @Column(columnDefinition = "text")
    private String xmlString;
}
