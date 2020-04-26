package com.atguigu.gmall.order.service;


import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cartt.entity.CartEntity;
import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.entity.OrderInfo;
import com.atguigu.gmall.oms.entity.OrderItem;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.atguigu.gmall.order.entity.UserInfo;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.sms.api.vo.SaleVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.entity.WareSkuLock;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderService {
    @Autowired
    UmsFeignClient umsFeignClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    PmsFeignClient pmsFeignClient;
    @Autowired
    SmsFeignClient smsFeignClient;
    @Autowired
    WmsFeignClient wmsFeignClient;
    @Autowired
    OmsFeignClient omsFeignClient;
    @Autowired
    RabbitTemplate rabbitTemplate;
    public static final String ORDER_ID = "order:id:";
    public OrderInfo orderConfirm() {
        UserInfo userInfo = LoginInterceptor.getThreadLocalValue();
        Long userId = userInfo.getUserId();
        if(userId == null){
            throw new GmallException("请登陆后再下单");
        }
        OrderInfo orderInfo = new OrderInfo();
        //设置商品信息
        List<CartEntity> cartEntities = cartFeignClient.getCartByUserId(userId).getData();
        if(CollectionUtils.isEmpty(cartEntities)){
            throw new GmallException("购物车为空");
        }
        List<OrderItem> orderItems = cartEntities.stream().map(cartEntity -> {
            OrderItem orderItem = new OrderItem();
            Long skuId = cartEntity.getSkuId();
            Integer count = cartEntity.getCount();
            orderItem.setSkuId(skuId);
            orderItem.setCount(count.longValue());
            SkuEntity skuEntity = pmsFeignClient.querySkuById(skuId).getData();
            orderItem.setTitle(skuEntity.getTitle());
            orderItem.setDefaultImages(skuEntity.getDefaultImage());
            orderItem.setPrice(skuEntity.getPrice());
            orderItem.setWeight(new BigDecimal(skuEntity.getWeight()));
            List<SaleVo> saleVos = smsFeignClient.getSaleBenifit(skuId).getData();
            orderItem.setSaleVos(saleVos);

            List<SkuAttrValueEntity> skuAttrValueEntities = pmsFeignClient.getSkuAttrValueBySkuId(skuId).getData();
            orderItem.setSkuAttrValueEntities(skuAttrValueEntities);

            List<WareSkuEntity> wareSkuEntities = wmsFeignClient.getListBySkuId(skuId.intValue()).getData();
            orderItem.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
            return orderItem;
        }).collect(Collectors.toList());
        orderInfo.setOrderItems(orderItems);
        //设置地址信息
        List<UserAddressEntity> userAddressEntities = umsFeignClient.getAddressListByUserId(userId).getData();
        orderInfo.setUserAddressEntities(userAddressEntities);
        //设置积分
        UserEntity userEntity = umsFeignClient.queryUserById(userId).getData();
        orderInfo.setIntegration(userEntity.getIntegration());
        //设置防重id,放入redis中
        String timeId = IdWorker.getTimeId();
        orderInfo.setOrderToken(timeId);
        //设置两小时的有效期，如果无货会重新生成OrderInfo
        redisTemplate.opsForValue().set(ORDER_ID + timeId,timeId,2, TimeUnit.HOURS);

        return orderInfo;
    }

    public OrderEntity orderSubmit(OrderSubmitVo orderSubmitVo) {
        //防重
        String orderToken = orderSubmitVo.getOrderToken();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Boolean execute = redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(ORDER_ID + orderToken), orderToken);
        if(!execute){
            throw new GmallException("页面失效，请刷新稍后重试");
        }
        //检验价格
        List<OrderItem> orderItems = orderSubmitVo.getOrderItems();
        BigDecimal curTotalPrice = orderItems.stream().map(orderItem -> {
            Long skuId = orderItem.getSkuId();
            SkuEntity skuEntity = pmsFeignClient.querySkuById(skuId).getData();
            return skuEntity.getPrice();
        }).reduce((a, b) -> a.add(b)).get();
        System.out.println("实时价格是：" + curTotalPrice);
        if(curTotalPrice.compareTo(orderSubmitVo.getTotalPrice()) != 0){
            throw new GmallException("页面失效，请刷新稍后重试");
        }
        //锁库存
        List<WareSkuLock> wareSkuLocks = orderSubmitVo.getOrderItems().stream().map(orderItem -> {
            WareSkuLock wareSkuLock = new WareSkuLock();
            wareSkuLock.setSkuId(orderItem.getSkuId());
            wareSkuLock.setCount(orderItem.getCount().intValue());
            wareSkuLock.setOrderToken(orderSubmitVo.getOrderToken());
            return wareSkuLock;
        }).collect(Collectors.toList());
        wareSkuLocks = wmsFeignClient.lockStock(wareSkuLocks).getData();
        boolean allMatch = wareSkuLocks.stream().allMatch(WareSkuLock::getStore);
//        int i = 10 / 0;
        //如果有无货的商品则返回该信息，结束方法
        if(!allMatch){
            throw new GmallException(JSON.toJSONString(wareSkuLocks));
        }
        //生成订单
        UserInfo userInfo = LoginInterceptor.getThreadLocalValue();
        Long userId = userInfo.getUserId();
        OrderEntity orderEntity;
        try {
            UserEntity userEntity = umsFeignClient.queryUserById(userId).getData();
            orderSubmitVo.setUsername(userEntity.getUsername());
            orderSubmitVo.setUserId(userInfo.getUserId());
            orderEntity = omsFeignClient.save(orderSubmitVo).getData();
            //设置订单的有效时期为24小时，此处方便测试设为1分钟，否则进入死信队列，补偿事务
            rabbitTemplate.convertAndSend("wms-exchange","order.ttl",orderSubmitVo.getOrderToken());
//            int i = 10 / 0;
        } catch (Exception e) {
            //TODO 订单生成失败，需要回滚锁定的库存信息
            rabbitTemplate.convertAndSend("wms-exchange","store.unlock",orderSubmitVo.getOrderToken());
            e.printStackTrace();
            throw new GmallException("生成订单失败，请稍后重试");
        }
        //mq删除购物车
        Map<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        List<Long> ids = orderSubmitVo.getOrderItems().stream().map(OrderItem::getSkuId).collect(Collectors.toList());
        map.put("skuIds",ids);
        rabbitTemplate.convertAndSend("cart-exchange","cart.delete",map);
        return orderEntity;
    }
}
