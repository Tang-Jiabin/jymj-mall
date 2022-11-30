package com.jymj.mall.pay.entity;

import com.jymj.mall.common.enums.OrderPayMethodEnum;
import com.jymj.mall.common.web.pojo.BaseEntity;
import lombok.*;

import javax.persistence.*;

/**
 * 商城支付信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-20
 */
@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mall_pay_info")
public class MallPayInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "mall_pay_info_pay_id_seq")
    @SequenceGenerator(name = "mall_pay_info_pay_id_seq",sequenceName = "mall_pay_info_pay_id_seq",allocationSize = 1)
    private Long payId;

    private Long userId;

    private Long orderId;

    private OrderPayMethodEnum payMethod;
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
