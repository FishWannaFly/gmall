package com.atguigu.gmall.oms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.mapper.OrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Component
public class OmsListener {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderMapper orderMapper;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order-queue",durable = "true"),
            exchange = @Exchange(value = "wms-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "order.update.dead"
    ))
    public void listener(String orderToken, Channel channel, Message message) throws IOException {
        try {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setStatus(4);
            orderMapper.update(orderEntity,new QueryWrapper<OrderEntity>().eq("order_sn",orderToken).eq("status",0));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            if(message.getMessageProperties().getRedelivered()){
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            e.printStackTrace();
        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order-success-queue",durable = "true"),
            exchange = @Exchange(value = "wms-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "order.success"
    ))
    public void listener2(String orderToken, Channel channel, Message message) throws IOException {
        try {
            if(!StringUtils.isEmpty(orderToken)){
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setStatus(1);
                orderMapper.update(orderEntity,new QueryWrapper<OrderEntity>().eq("order_sn",orderToken).eq("status",0));
                rabbitTemplate.convertAndSend("wms-exchange","stock.minus",orderToken);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            if(message.getMessageProperties().getRedelivered()){
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            e.printStackTrace();
        }

    }

}
