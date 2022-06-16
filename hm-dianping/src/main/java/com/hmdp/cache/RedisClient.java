package com.hmdp.cache;

import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Map;

import static com.hmdp.constant.KeyConstant.LOCK_CACHE_SHOP_VALUE;

/**
 * <h3>Redis 工具类</h3>
 * TODO AOP 打印日志
 */
@Slf4j
@Component
public class RedisClient {

    @Resource
    private StringRedisTemplate template;

    public String get(String key){
        return template.opsForValue().get(key);
    }

    public void set(String key, String value){
        template.opsForValue().set(key, value);
    }

    public void set(String key, String value, Duration time){
        template.opsForValue().set(key, value, time);
    }

    public void hset(String key, Map<?, ?> fields){
        template.opsForHash().putAll(key, fields);
    }

    public Object hget(String key, String field){
         return template.opsForHash().get(key, field);
    }

    public boolean expire(String key, Duration time){
        return BooleanUtil.isTrue(template.expire(key, time));
    }

    public Map<Object, Object> hgetAll(String key){
        return template.opsForHash().entries(key);
    }

    public boolean del(String key){
        return BooleanUtil.isTrue(template.delete(key));
    }

    public boolean setnx(String key){
        return BooleanUtil.isTrue(template.opsForValue().setIfAbsent(
                key, LOCK_CACHE_SHOP_VALUE, Duration.ofSeconds(10)));
    }
}
