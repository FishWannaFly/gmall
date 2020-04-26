package com.atguigu.gmall.wms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    //延时交换机和死信交换机公用一个。设置30s过期
    @Bean
    public Queue ttlQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","wms-exchange");
        arguments.put("x-dead-letter-routing-key","order.dead");
        arguments.put("x-message-ttl",60000);
        return new Queue("ttl-queue",true,false,false,arguments);
    }
    @Bean
    public Queue stockTtlQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","wms-exchange");
        arguments.put("x-dead-letter-routing-key","store.unlock");
        arguments.put("x-message-ttl",70000);
        return new Queue("stock-ttl-queue",true,false,false,arguments);
    }
    @Bean
    public Binding stockTtlBinding(){
        return new Binding("stock-ttl-queue", Binding.DestinationType.QUEUE,
                "wms-exchange","stock.ttl",null);
    }
    @Bean
    public Binding ttlBinding(){
        return new Binding("ttl-queue", Binding.DestinationType.QUEUE,
                "wms-exchange","order.unlock",null);
    }
    @Bean
    public Queue deadQueue(){
        return new Queue("order-dead-queue",true,false,false,null);
    }
    @Bean
    public Binding deadBinding(){
        return new Binding("order-dead-queue", Binding.DestinationType.QUEUE,
                "wms-exchange","order.dead",null);
    }
}
