package com.jymj.mall.order.config;


import com.jymj.mall.common.enums.OrderStatusEnum;
import com.jymj.mall.order.dto.OrderDTO;
import com.jymj.mall.order.entity.MallOrder;
import com.jymj.mall.order.service.OrderService;
import com.jymj.mall.order.vo.MallOrderInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MallOrderRabbitMQConsumer {

    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MQConfig.QUEUE_ORDER)
    public void handlerOrder(@Payload MallOrderInfo order, Message message) {

        log.info("新建订单id：{}", order.getOrderId());

        // 发送该订单至核验队列
        rabbitTemplate.convertAndSend(
                MQConfig.EXCHANGE_DELAY,
                MQConfig.ROUTING_KEY_QUEUE_CHECK_ORDER,
                order);
    }

    // 核验队列（延迟）后 会将消息发送至死信队列。死信队列判断该订单是否过期
    @RabbitListener(queues = MQConfig.QUEUE_DELAY)
    public void handlerDelayOrder(@Payload MallOrderInfo order, Message message) {

        // 查找数据库该订单是否已支付
        Optional<MallOrder> mallOrderOptional = orderService.findById(order.getOrderId());
        mallOrderOptional.ifPresent(e -> {
            switch (e.getOrderStatus()){
                case UNPAID:
                    log.info("订单未付款：{}",e.getOrderId());
                    orderService.update(OrderDTO.builder().orderId(e.getOrderId()).statusEnum(OrderStatusEnum.CLOSED).build());
                    log.info("订单id:{} 长时间未支付，已关闭", order.getOrderId());
                    break;
                case CANCELED:
                    log.info("订单已取消：{}",e.getOrderId());
                    break;
                case COMPLETED:
                    log.info("订单已完成：{}",e.getOrderId());
                    break;
                case UNSHIPPED:
                    log.info("订单未发货：{}",e.getOrderId());
                    break;
                case UNRECEIVED:
                    log.info("订单未收货：{}",e.getOrderId());
                    break;
                case AFTER_SALES:
                    log.info("订单售后：{}",e.getOrderId());
                    break;
                case CLOSED:
                    log.info("订单已关闭：{}",e.getOrderId());
                    break;
                default:
                    log.warn("订单状态未知：{}",e.getOrderId());
                    break;
            }
        });
    }

    // 支付成功
    @RabbitListener(queues = MQConfig.QUEUE_PAY_SUCCESS)
    public void handlerPayOrder(@Payload Long orderId, Message message) {
        log.info("订单id：{}  付款成功", orderId);
    }
}
