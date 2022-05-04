package com.xinlvyao.send.sender;

import com.xinlvyao.send.config.ExchangeConfig;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sender {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void sendMsg(String msg, MessagePostProcessor messagePostProcessor){
        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //发送消息到延时队列中
        rabbitTemplate.convertAndSend(ExchangeConfig.TOPIC_EXCHANGE,"order.ttl",msg,messagePostProcessor);
    }
}
