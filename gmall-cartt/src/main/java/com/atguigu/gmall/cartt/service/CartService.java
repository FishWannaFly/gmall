package com.atguigu.gmall.cartt.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cartt.entity.CartEntity;
import com.atguigu.gmall.cartt.entity.UserInfo;
import com.atguigu.gmall.cartt.feign.PmsFeignClient;
import com.atguigu.gmall.cartt.feign.SmsFeignClient;
import com.atguigu.gmall.cartt.feign.WmsFeignClient;
import com.atguigu.gmall.cartt.interceptor.LoginInterceptor;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.sms.api.vo.SaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    PmsFeignClient pmsFeignClient;
    @Autowired
    SmsFeignClient smsFeignClient;
    @Autowired
    WmsFeignClient wmsFeignClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    private static final String CART_PREFIX = "cart:";
    private static final String PRICE_PREFIX = "price:";
    public void addCart(Long skuId, Integer count) {
        String key = getKey();
        //一定要是字符串作为key查询，否则查询不到。
        //如果购物车有改skuid商品则增加数量
        if(redisTemplate.boundHashOps(key).hasKey(skuId.toString())){
            String str = (String) redisTemplate.boundHashOps(key).get(skuId.toString());
            CartEntity cartEntity = JSON.parseObject(str, CartEntity.class);
            cartEntity.setCount(cartEntity.getCount() + count);
            redisTemplate.boundHashOps(key).put(skuId.toString(),JSON.toJSONString(cartEntity));
            return ;
        }
        CartEntity cartEntity = new CartEntity();
        SkuEntity skuEntity = pmsFeignClient.querySkuById(skuId).getData();
        List<SaleVo> saleVos = smsFeignClient.getSaleBenifit(skuId).getData();
        List<WareSkuEntity> wareSkuEntities = wmsFeignClient.getListBySkuId(skuId.intValue()).getData();
        List<SkuAttrValueEntity> skuAttrValueEntities = pmsFeignClient.getSkuAttrValueBySkuId(skuId).getData();
        cartEntity.setSkuId(skuId);
        cartEntity.setCheck(true);
        cartEntity.setCount(count);
        cartEntity.setDefalutImage(skuEntity.getDefaultImage());
        cartEntity.setPrice(skuEntity.getPrice());
        cartEntity.setSaleAttrValue(skuAttrValueEntities);
        cartEntity.setSaleInfo(saleVos);
        cartEntity.setTitle(skuEntity.getTitle());
        cartEntity.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock()-wareSkuEntity.getStockLocked()-count>0));
        //缓存一份价格
        redisTemplate.opsForValue().set(PRICE_PREFIX + skuId,cartEntity.getPrice().toString());

        redisTemplate.boundHashOps(key).put(cartEntity.getSkuId().toString(), JSON.toJSONString(cartEntity));
    }

    public String getKey() {
        UserInfo userInfo = LoginInterceptor.getThreadLocalValue();
        Long userId = userInfo.getUserId();
        String userKey = userInfo.getUserKey();
        String key;
        if(!StringUtils.isEmpty(userKey) && userId != null){
            key = CART_PREFIX + userId;
        }else{
            key = CART_PREFIX + userKey;
        }
        return key;
    }

    public List<CartEntity> getCart() {
        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        //未登录 则直接返回购物车数据
        UserInfo userInfo = LoginInterceptor.getThreadLocalValue();
        Long userId = userInfo.getUserId();
        String userKey = userInfo.getUserKey();
        if(userId == null){
            List<Object> values = hashOps.values();
            List<CartEntity> cartEntities = values.stream().map(value -> {
                CartEntity cartEntity = JSON.parseObject(value.toString(), CartEntity.class);
                String priceStr = redisTemplate.opsForValue().get(PRICE_PREFIX + cartEntity.getSkuId());
                BigDecimal currentPrice = JSON.parseObject(priceStr, BigDecimal.class);
                cartEntity.setCurrentPrice(currentPrice);
                return cartEntity;
            }).collect(Collectors.toList());
            return cartEntities;
        }
        //登录则获取购物车数据 和未登录购物车数据合并
        BoundHashOperations<String, Object, Object> unloginHashOps = redisTemplate.boundHashOps(CART_PREFIX + userKey);
        List<Object> unloginValues = unloginHashOps.values();
        unloginValues.stream().forEach(unloginValue -> {
            CartEntity unloginCartEntity = JSON.parseObject((String) unloginValue, CartEntity.class);
            //如果未登录购物车包含已登陆的商品，则增加数量即可
            if (hashOps.hasKey(unloginCartEntity.getSkuId().toString())) {
                String loginStr = (String) hashOps.get(unloginCartEntity.getSkuId().toString());
                CartEntity loginCartEntity = JSON.parseObject(loginStr, CartEntity.class);
                loginCartEntity.setCount(loginCartEntity.getCount() + unloginCartEntity.getCount());
                hashOps.put(loginCartEntity.getSkuId().toString(), JSON.toJSONString(loginCartEntity));
            } else {
                hashOps.put(unloginCartEntity.getSkuId().toString(), JSON.toJSONString(unloginCartEntity));
            }
        });
        //删除未登录购物车数据
        redisTemplate.delete(CART_PREFIX + userKey);
        //获取最新的购物车
        return hashOps.values().stream().map(value->{
            CartEntity cartEntity = JSON.parseObject(value.toString(), CartEntity.class);
            String priceStr = redisTemplate.opsForValue().get(PRICE_PREFIX + cartEntity.getSkuId());
            BigDecimal currentPrice = JSON.parseObject(priceStr, BigDecimal.class);
            cartEntity.setCurrentPrice(currentPrice);
            return cartEntity;
        }).collect(Collectors.toList());
    }

    public void updateCount(Long skuId, Integer count) {
        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        String str = (String) hashOps.get(skuId.toString());
        CartEntity cartEntity = JSON.parseObject(str, CartEntity.class);
        cartEntity.setCount(count);
        hashOps.put(skuId.toString(),JSON.toJSONString(cartEntity));
    }

    public void deleteBySkuId(Long skuId) {
        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        hashOps.delete(skuId.toString());
    }

    public List<CartEntity> getCartByUserId(Long userId) {
        List<Object> values = redisTemplate.boundHashOps(CART_PREFIX + userId).values();
        return values.stream().map(value ->{
            CartEntity cartEntity = JSON.parseObject((String) value, CartEntity.class);
            return cartEntity;
        }).collect(Collectors.toList());
    }
}
