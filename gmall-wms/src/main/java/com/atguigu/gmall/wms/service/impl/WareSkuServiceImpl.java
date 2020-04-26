package com.atguigu.gmall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.wms.entity.WareSkuLock;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.wms.mapper.WareSkuMapper;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSkuEntity> implements WareSkuService {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    public static String LOCK_NAME = "store:lock:";
    public static String PRE_KEY = "store:info:";
    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional
    public void lockStock(List<WareSkuLock> list) {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        //遍历集合锁定库存
        list.stream().forEach(wareSkuLock -> {
            lockSku(wareSkuLock);
        });
        boolean flag = list.stream().anyMatch(wareSkuLock -> !wareSkuLock.getStore());
        if(flag){
            //如果有零库存的则全部回滚
            list.stream().filter(wareSkuLock -> wareSkuLock.getStore()).forEach(wareSkuLock -> {
                baseMapper.unlockWare(wareSkuLock.getWareSkuId(),wareSkuLock.getCount());
            });
            return;
        }
        //走到这，则代表有库存,保存到redis中方便回滚
        redisTemplate.opsForValue().set(PRE_KEY + list.get(0).getOrderToken(), JSON.toJSONString(list));
        //设置锁定库存的时间，到时间自动解锁，这个时间需要比订单的过期时间要长一点。
        rabbitTemplate.convertAndSend("wms-exchange","stock.ttl",list.get(0).getOrderToken());
    }

    @Transactional
    public void lockSku(WareSkuLock wareSkuLock) {
        Long skuId = wareSkuLock.getSkuId();
        Integer count = wareSkuLock.getCount();
        //锁库存
        RLock lock = redissonClient.getFairLock(LOCK_NAME + wareSkuLock.getSkuId());
        lock.lock();
        List<WareSkuEntity> list = baseMapper.ableWareSku(skuId,count);
        //无库存直接设置为false返回
        if(CollectionUtils.isEmpty(list)){
            wareSkuLock.setStore(false);
            lock.unlock();
            return ;
        }
        Long id = list.get(0).getId();
        int i = baseMapper.lockWare(id,count);
        if(i == 1){
            wareSkuLock.setStore(true);
            wareSkuLock.setWareSkuId(id);
        }else{
            wareSkuLock.setStore(false);
        }
        lock.unlock();
    }

}