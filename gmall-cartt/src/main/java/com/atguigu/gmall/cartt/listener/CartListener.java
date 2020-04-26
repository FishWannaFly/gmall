package com.atguigu.gmall.cartt.listener;

import com.atguigu.gmall.cartt.feign.PmsFeignClient;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CartListener {
    private static final String PRICE_PREFIX = "price:";
    @Autowired
    PmsFeignClient pmsFeignClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    private static final String CART_PREFIX = "cart:";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart-delete-queue",durable = "true"),
            exchange = @Exchange(value = "cart-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "cart.delete"
    ))
    public void cartListener(Map<String,Object> map, Channel channel, Message message) throws IOException {
        try {
            Long userId = (Long) map.get("userId");
            List<Long> ids = (List<Long>) map.get("skuIds");
            List<String> idsStr = ids.stream().map(id -> id.toString()).collect(Collectors.toList());
            redisTemplate.boundHashOps(CART_PREFIX + userId).delete(idsStr.toArray());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
            if(message.getMessageProperties().getRedelivered()){
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart-update-queue",durable = "true"),
            exchange = @Exchange(value = "spu-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "spu.update"
    ))
    public void listener(Long spuId){
        List<SkuEntity> skuEntities = pmsFeignClient.getListBySpuid(spuId).getData();
        skuEntities.forEach(skuEntity -> {
            Long skuId = skuEntity.getId();
            BigDecimal price = skuEntity.getPrice();
            redisTemplate.opsForValue().set(PRICE_PREFIX + skuId,price.toString());
        });

    }
}
