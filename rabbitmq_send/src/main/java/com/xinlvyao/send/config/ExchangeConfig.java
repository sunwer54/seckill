package com.xinlvyao.send.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeConfig {
    public static final String DIRECT_EXCHANGE="direct_exchange";
    public static final String TOPIC_EXCHANGE="topic_exchange";
    /**
     * 点对点的定向交换机
     */
    @Bean("directExchange")
    public DirectExchange directExchange(){
        //参数一：交换机名称，参数二：是否持久化，参数三：是否自动删除
        return new DirectExchange(DIRECT_EXCHANGE,true,false);
    }
    /**
     * 按规则匹配交换机
     */
    @Bean("topicExchange")
    public TopicExchange topicExchange(){
        //参数一：交换机名称，参数二：是否持久化，参数三：是否自动删除
        return new TopicExchange(TOPIC_EXCHANGE,true,false);
    }
}
