package com.jymj.mall.pay.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.jymj.mall.common.constants.PayConstants;
import com.jymj.mall.common.enums.OrderPayMethodEnum;
import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.redis.RedisUtils;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.order.api.OrderFeignClient;
import com.jymj.mall.order.dto.OrderPaySuccess;
import com.jymj.mall.order.vo.MallOrderInfo;
import com.jymj.mall.pay.entity.MallWeChatPayInfo;
import com.jymj.mall.pay.repository.MallWeChatPayInfoRepository;
import com.jymj.mall.pay.service.MallPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 微信
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MallPayServiceImpl implements MallPayService {

    private final OrderFeignClient orderFeignClient;
    private final MallWeChatPayInfoRepository payInfoRepository;
    private final RedisUtils redisUtils;


    @Override
    public WxPayUnifiedOrderRequest createWeChatPayOrder(String ip, String orderNo) {

        Result<MallOrderInfo> orderInfoResult = orderFeignClient.getOrderByNo(orderNo);
        if (!Result.isSuccess(orderInfoResult)) {
            throw new BusinessException("订单不存在或已过期");
        }
        MallOrderInfo orderInfo = orderInfoResult.getData();
        if (orderInfo.getOrderStatus() != OrderStatusEnum.UNPAID) {
            throw new BusinessException("订单已付款或已过期");
        }

        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(PayConstants.SUBJECT);
        orderRequest.setOutTradeNo(orderInfo.getOrderNo());
        orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(orderInfo.getAmountPayable().toString()));//元转成分
        orderRequest.setOpenid(UserUtils.getOpenId());
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setTradeType(PayConstants.WECHAT_PAY_TRADE_TYPE_JSAPI);
        orderRequest.setTimeStart(LocalDateTimeUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss"));
        orderRequest.setTimeExpire(LocalDateTimeUtil.format(LocalDateTime.now().plusMinutes(30), "yyyyMMddHHmmss"));
        return orderRequest;
    }

    @Override
    public void WeChatPayNotify(WxPayOrderNotifyResult notifyResult) {
        log.info("微信支付回调：{}", notifyResult.toString());
        if ("SUCCESS".equals(notifyResult.getResultCode())) {
            Result<MallOrderInfo> orderInfoResult = orderFeignClient.getOrderByNo(notifyResult.getOutTradeNo());
            if (!Result.isSuccess(orderInfoResult)) {
                log.error("微信支付回调：订单不存在：{}", notifyResult.getOutTradeNo());
                return;
            }
            MallOrderInfo orderInfo = orderInfoResult.getData();
            if (orderInfo.getOrderStatus() != OrderStatusEnum.UNPAID) {
                log.error("微信支付回调：订单状态错误：{}", orderInfo.getOrderStatus());
                return;
            }

            String key = "mall-pay:no:" + orderInfo.getOrderNo();

            boolean existence = redisUtils.hasKey(key);
            if (!existence) {
                OrderPaySuccess orderPaySuccess = new OrderPaySuccess();
                orderPaySuccess.setOrderId(orderInfo.getOrderId());
                BigDecimal totalFree = new BigDecimal(String.valueOf(notifyResult.getTotalFee())).divide(new BigDecimal("100"), 2, RoundingMode.HALF_EVEN);
                orderPaySuccess.setAmountActuallyPaid(totalFree);
                orderPaySuccess.setOrderPayMethod(OrderPayMethodEnum.WEI_XIN_PAY);
                orderPaySuccess.setPayTime(DateUtil.parse(notifyResult.getTimeEnd()));

                orderFeignClient.paySuccess(orderPaySuccess);
                redisUtils.set(key, 1, 30, TimeUnit.MINUTES);

                MallWeChatPayInfo weChatPayInfo = new MallWeChatPayInfo();
                weChatPayInfo.setOrderId(orderInfo.getOrderId());
                weChatPayInfo.setOpenid(notifyResult.getOpenid());
                weChatPayInfo.setIsSubscribe(notifyResult.getIsSubscribe());
                weChatPayInfo.setTradeType(notifyResult.getTradeType());
                weChatPayInfo.setBankType(notifyResult.getBankType());
                weChatPayInfo.setTotalFee(notifyResult.getTotalFee());
                weChatPayInfo.setFeeType(notifyResult.getFeeType());
                weChatPayInfo.setCashFee(notifyResult.getCashFee());
                weChatPayInfo.setTransactionId(notifyResult.getTransactionId());
                weChatPayInfo.setOutTradeNo(notifyResult.getOutTradeNo());
                weChatPayInfo.setTimeEnd(notifyResult.getTimeEnd());
                weChatPayInfo.setReturnCode(notifyResult.getReturnCode());
                weChatPayInfo.setResultCode(notifyResult.getResultCode());
                weChatPayInfo.setAppid(notifyResult.getAppid());
                weChatPayInfo.setMchId(notifyResult.getMchId());
                weChatPayInfo.setNonceStr(notifyResult.getNonceStr());
                weChatPayInfo.setSign(notifyResult.getSign());
                weChatPayInfo.setXmlString(notifyResult.getXmlString());
                payInfoRepository.save(weChatPayInfo);


            }
        }
    }

    @Override
    public WxPayRefundRequest refund(String orderNo) {
        Result<MallOrderInfo> orderInfoResult = orderFeignClient.getOrderByNo(orderNo);
        if (!Result.isSuccess(orderInfoResult)) {
            throw new BusinessException("订单不存在");
        }
        MallOrderInfo orderInfo = orderInfoResult.getData();
        return WxPayRefundRequest.newBuilder()
                .outTradeNo(orderInfo.getOrderNo())
                .outRefundNo(orderInfo.getOrderNo() + "-" + RandomUtil.randomNumbers(4))
                .refundFee(BaseWxPayRequest.yuanToFen(orderInfo.getAmountActuallyPaid().toString())).build();
    }
}
