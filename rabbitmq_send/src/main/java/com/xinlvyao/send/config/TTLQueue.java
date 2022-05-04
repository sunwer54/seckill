package com.xinlvyao.send.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 过期队列配置类，用来接收待支付的订单
 */
@Configuration
public class TTLQueue {
    //队列名称
    public static final String TTL_QUEUE="ttl_queue";
    //路由键名称
    public static final String TTL_ROUTING_KEY="#.ttl";//#表示匹配所有以.ttl为后缀的路由键
    @Bean("ttlQueue")
    public Queue ttlQueue(){
        Map<String,Object> args = new HashMap<>();
        //消息过期时间设置为10s（"x-message-ttl"是固定此写法，不能随便写）
        args.put("x-message-ttl",30*1000);
        //指定转发到死信队列的交换机的名称（"x-dead-letter-exchange"该名称是固定此写法，不能随便写）
        args.put("x-dead-letter-exchange",ExchangeConfig.DIRECT_EXCHANGE);
        //指定转发到死信队列的路由键的名称（"x-dead-letter-routing-key"该名称是固定此写法，不能随便写）
        args.put("x-dead-letter-routing-key",DLXQueueConfig.DLX_ROUTING_KEY);
        //参数一：队列名称，参数二：是否持久化，参数三：是否独有，参数四：是否自动删除，参数五：封装队列的配置参数
        return new Queue(TTL_QUEUE,true,false,false,args);
    }
    ////绑定队列到指定交换机和指定路由键
    @Bean("ttlBind")
    public Binding binding(@Autowired @Qualifier("topicExchange")TopicExchange topicExchange){
        //@Qualifier这个注解是在spring中能从多个相同的类型bean对象中找到我们想要的
        return BindingBuilder.bind(ttlQueue()).to(topicExchange).with(TTL_ROUTING_KEY);
    }
}
