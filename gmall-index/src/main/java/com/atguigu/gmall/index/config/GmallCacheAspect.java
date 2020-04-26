package com.atguigu.gmall.index.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class GmallCacheAspect {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Around("@annotation(com.atguigu.gmall.index.config.RedisCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Class<?> returnType = method.getReturnType();
            RedisCache annotation = method.getAnnotation(RedisCache.class);
            String prefix = annotation.prefix();
            String lock = annotation.lock();
            int random = annotation.random();
            int timeout = annotation.timeout();
            Object[] args = joinPoint.getArgs();
            String param = Arrays.asList(args).toString();
            String value = redisTemplate.opsForValue().get(prefix + param);
            if(!StringUtils.isEmpty(value)){
                Object parseObject = JSON.parseObject(value, returnType);
                return parseObject;
            }
            RLock rLock = redissonClient.getLock(lock + param);
            rLock.lock();
            //许多线程会在此阻塞，拿到锁后先判断缓存是否已经有值了。
            value = redisTemplate.opsForValue().get(prefix + param);
            if(!StringUtils.isEmpty(value)){
                Object parseObject = JSON.parseObject(value, returnType);
                rLock.unlock();
                return parseObject;
            }
            Object proceed = joinPoint.proceed(args);
            if(proceed instanceof Collection){
                if(CollectionUtils.isEmpty((Collection)proceed)){
                    redisTemplate.opsForValue().set(prefix+param,JSON.toJSONString(proceed),5, TimeUnit.MINUTES);
                }else{
                    redisTemplate.opsForValue().set(prefix+param,JSON.toJSONString(proceed),timeout+new Random().nextInt(random), TimeUnit.MINUTES);
                }
            }else if(proceed == null){
                redisTemplate.opsForValue().set(prefix+param,JSON.toJSONString(proceed),5, TimeUnit.MINUTES);
            }else{
                redisTemplate.opsForValue().set(prefix+param,JSON.toJSONString(proceed),timeout+new Random().nextInt(random), TimeUnit.MINUTES);
            }
            rLock.unlock();
            return proceed;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }
}
