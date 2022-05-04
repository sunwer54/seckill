package com.xinlvyao.send.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信队列配置类，用来接收过期队列中过期的消息
 */
@Configuration
public class DLXQueueConfig {
    //队列名称
    public static final String DLX_QUEUE = "dlx_queue";
    //路由键
    public static final String DLX_ROUTING_KEY = "dlx.ttl.key";

    //配置死信队列
    @Bean("dlxQueue")
    public Queue dlxQueue(){
        /*
        第一个参数：队列名称,第二个参数：是否持久化，第三个参数：是否独有，第四个参数：是否自动删除
         */
        return new Queue(DLX_QUEUE,true,false,false);
    }

    //绑定队列到指定交换机和指定路由键
    @Bean("dlxBind")
    public Binding binding(@Autowired @Qualifier("directExchange") DirectExchange directExchange){
        //@Qualifier这个注解是在spring中能从多个相同的类型bean对象中找到我们想要的
        return BindingBuilder.bind(dlxQueue()).to(directExchange).with(DLX_ROUTING_KEY);
    }
}
