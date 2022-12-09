package com.jymj.mall.common.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * MQ配置
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-19
 */
@Configuration
public class MQConfig {
    //交换机
    public static final String EXCHANGE_DELAY = "EXCHANGE_DELAY";

    // 订单队列
    public static final String QUEUE_ORDER = "QUEUE_ORDER";
    //死信队列 用来接收延迟队列的消息
    public static final String QUEUE_DELAY = "QUEUE_DELAY";
    // 检测订单队列 （延迟队列）时间过期后，该数据会被推送至死信队列
    public static final String QUEUE_CHECK_ORDER = "QUEUE_CHECK_ORDER";
    // 订单支付成功路由键
    public static final String QUEUE_PAY_SUCCESS = "QUEUE_PAY_SUCCESS";

    //订单路由键
    public static final String ROUTING_KEY_QUEUE_ORDER = "ROUTING_KEY_QUEUE_ORDER";
    // 成功支付路由健
    public static final String ROUTING_KEY_QUEUE_PAY_SUCCESS = "ROUTING_KEY_QUEUE_PAY_SUCCESS";
    // 订单检测路由键
    public static final String ROUTING_KEY_QUEUE_CHECK_ORDER = "ROUTING_KEY_QUEUE_CHECK_ORDER";
    // 死信路由键
    public static final String ROUTING_KEY_QUEUE_DELAY = "ROUTING_KEY_QUEUE_DELAY";

    //定义交换机

    @Bean
    public Exchange exchangeDelay(){
        return ExchangeBuilder.topicExchange(EXCHANGE_DELAY).durable(true).build();
    }
    //检测订单
    @Bean(QUEUE_CHECK_ORDER)
    public Queue queueCheckOrder(){
        Map<String,Object> params = new HashMap<>();
        //过期的消息给哪个交换机的名字
        params.put("x-dead-letter-exchange", EXCHANGE_DELAY);
        //设置死信交换机把过期的消息给哪个路由键接收
        params.put("x-dead-letter-routing-key", ROUTING_KEY_QUEUE_DELAY);
        params.put("x-message-ttl",1000*60*30);

        return new Queue(QUEUE_CHECK_ORDER, true, false, false, params);

    }
    //死信队列
    @Bean(QUEUE_DELAY)
    public Queue queueDelay(){
        return new Queue(QUEUE_DELAY,true);
    }
    // 支付成功队列
    @Bean(QUEUE_PAY_SUCCESS)
    public Queue queuePaySuccess(){

        return new Queue(QUEUE_PAY_SUCCESS,true);
    }
    // 订单队列
    @Bean(QUEUE_ORDER)
    public Queue queueOrder(){
        return new Queue(QUEUE_ORDER,true);
    }
    // 绑定队列与交换器
    @Bean
    public Binding queueOrderBinding(){
        return BindingBuilder.bind(queueOrder()).to(exchangeDelay()).with(ROUTING_KEY_QUEUE_ORDER).noargs();
    }

    @Bean
    public Binding queueCheckOrderBinding(){
        return BindingBuilder.bind(queueCheckOrder()).to(exchangeDelay()).with(ROUTING_KEY_QUEUE_CHECK_ORDER).noargs();
    }
    @Bean
    public Binding queueDelayBinding(){
        return BindingBuilder.bind(queueDelay()).to(exchangeDelay()).with(ROUTING_KEY_QUEUE_DELAY).noargs();
    }

    @Bean
    public Binding queuePayBinding(){
        return BindingBuilder.bind(queuePaySuccess()).to(exchangeDelay()).with(ROUTING_KEY_QUEUE_PAY_SUCCESS).noargs();
    }
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
