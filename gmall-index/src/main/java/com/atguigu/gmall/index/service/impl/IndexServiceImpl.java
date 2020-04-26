package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.config.RedisCache;
import com.atguigu.gmall.index.feign.PmsFeignClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.CategoryVo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import springfox.documentation.spring.web.json.Json;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    PmsFeignClient pmsFeignClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    private final String CATE_PREFIX = "index:cates:";
    @Autowired
    RedissonClient redissonClient;
    @Override
    public List<CategoryEntity> categoryListLv1() {
        return pmsFeignClient.getParent(0L).getData();
    }

    @Override
    @RedisCache(prefix = CATE_PREFIX)
    public List<CategoryVo> categoryListLv23(Long pid) {

        List<CategoryVo> categoryVoList = pmsFeignClient.getChildren(pid).getData();
        return categoryVoList;
    }

    @Override
    public synchronized void test() {
        //设置过期时间，防止死锁
        String uuid = UUID.randomUUID().toString();
        Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", uuid,3,TimeUnit.SECONDS);
        if(flag){
            String countStr = redisTemplate.opsForValue().get("count");
            redisTemplate.opsForValue().set("count",Integer.parseInt(countStr)+1+"");
            String lock = redisTemplate.opsForValue().get("lock");
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList("lock"),uuid);
            //这样不具有原子性
//            if(StringUtils.equals(lock,uuid)){
//                redisTemplate.delete("lock");
//            }
        }else{
            test();
        }


    }
}
