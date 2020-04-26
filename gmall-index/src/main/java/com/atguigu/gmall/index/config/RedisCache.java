package com.atguigu.gmall.index.config;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisCache {
    /**
     * 缓存key的前缀
     * @return
     */
    String prefix() default "";

    /**
     * 单位：min
     * 默认设置缓存有效时长为1天=60*24=1440。
     * @return
     */
    int timeout() default 1440;

    /**
     * 防止击穿的锁的key值
     * @return
     */
    String lock() default "lock";

    /**
     * 防止雪崩的随机值
     * @return
     */
    int random() default 5;

}
