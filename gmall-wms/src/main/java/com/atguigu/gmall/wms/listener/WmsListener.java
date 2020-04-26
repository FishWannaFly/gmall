package com.atguigu.gmall.wms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.entity.WareSkuLock;
import com.atguigu.gmall.wms.mapper.WareSkuMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Component
public class WmsListener {
    @Autowired
    WareSkuMapper wareSkuMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    public static String PRE_KEY = "store:info:";
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "wms-queue",durable = "true"),
            exchange = @Exchange(value = "wms-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "store.unlock"
    ))
    public void listener(String orderToken, Channel channel, Message message) throws IOException {
        try {
            String str = redisTemplate.opsForValue().get(PRE_KEY + orderToken);
            if(StringUtils.isEmpty(str)){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                return;
            }
            List<WareSkuLock> wareSkuLocks = JSON.parseArray(str,WareSkuLock.class);
            if(CollectionUtils.isEmpty(wareSkuLocks)){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                return;
            }
            wareSkuLocks.stream().forEach(wareSkuLock -> {
                wareSkuMapper.unlockWare(wareSkuLock.getWareSkuId(),wareSkuLock.getCount());
            });
            redisTemplate.delete(PRE_KEY + orderToken);
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
            value = @Queue(value = "order-dead-queue",durable = "true"),
            exchange = @Exchange(value = "wms-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "order.dead"
    ))
    public void deadListener(String orderToken, Channel channel, Message message) throws IOException {
        try {
//            System.out.println(wareSkuLocks);
            //解锁库存
            rabbitTemplate.convertAndSend("wms-exchange","store.unlock",orderToken);
            //修改订单状态
            rabbitTemplate.convertAndSend("wms-exchange","order.update.dead",orderToken);
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
            value = @Queue(value = "stock-minus-queue",durable = "true"),
            exchange = @Exchange(value = "wms-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "stock.minus"
    ))
    public void minusStock(String orderToken, Channel channel, Message message) throws IOException {
        try {
            String str = redisTemplate.opsForValue().get(PRE_KEY + orderToken);
            if(!StringUtils.isEmpty(str)){
                List<WareSkuLock> wareSkuLocks = JSON.parseArray(str,WareSkuLock.class);
                if(!CollectionUtils.isEmpty(wareSkuLocks)){
                    wareSkuLocks.forEach(wareSkuLock -> {
                        wareSkuMapper.minusStore(wareSkuLock.getWareSkuId(),wareSkuLock.getCount());
                    });
                }
            }
            redisTemplate.delete(PRE_KEY + orderToken);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            if(message.getMessageProperties().getRedelivered()){
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            e.printStackTrace();
        }

    }

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "store-minus-queue",durable = "true"),
//            exchange = @Exchange(value = "wms-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
//            key = "store.minus"
//    ))
//    public void storeListener(String orderToken, Channel channel, Message message) throws IOException {
//        try {
//            if(!StringUtils.isEmpty(orderToken)){
//                String str = redisTemplate.opsForValue().get(PRE_KEY + orderToken);
//                List<WareSkuLock> wareSkuLocks = JSON.parseArray(str,WareSkuLock.class);
//                wareSkuLocks.forEach(wareSkuLock -> {
//                    wareSkuMapper.minusStore(wareSkuLock.getWareSkuId(),wareSkuLock.getCount());
//                });
//            }
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//        } catch (Exception e) {
//            if(message.getMessageProperties().getRedelivered()){
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
//            }
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
//            e.printStackTrace();
//        }
//
//    }
}
