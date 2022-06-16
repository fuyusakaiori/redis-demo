package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.hmdp.cache.RedisClient;
import com.hmdp.dto.Response;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hmdp.constant.KeyConstant.CACHE_SHOP_KEY;
import static com.hmdp.constant.KeyConstant.LOCK_CACHE_SHOP_INFO_KEY;
import static com.hmdp.constant.MessageConstant.*;
import static com.hmdp.constant.TimeConstant.NULL_EXPIRE_TIME;
import static com.hmdp.constant.TimeConstant.SHOP_INFO_EXPIRE_TIME;

/**
 * <h3>商铺服务实现类</h3>
 *
 * @author 冬坂五百里
 * @since 2022-06-15
 */
@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private RedisClient redisClient;

    /**
     * <h3>1. 防止缓存穿透主要就是两种手段</h3>
     * <h3>2. 第一种就是布隆过滤器, 第二种就是设置空值</h3>
     */
    @Override
    public Response getShopById(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 1. 查询 Redis
        Map<Object, Object> fields = redisClient.hgetAll(key);
        // 2. 检查缓存是否有效
        Response response = checkRedis(fields);
        if (response != null)
            return response;
        // 分布式锁: 如果没有在缓存中查到, 那么就证明缓存失效, 需要重建缓存, 那么就需要上锁
        Shop shop = null;
        String lockKey = LOCK_CACHE_SHOP_INFO_KEY + id;
        try {
            while (!redisClient.setnx(lockKey)){
                TimeUnit.SECONDS.sleep(1);
            }
            // 双重检查缓存, 获取缓存后返回
            if ((response = checkRedis(redisClient.hgetAll(key))) != null)
                return response;
            // 3. 如果不存在商铺信息, 那么就去数据库中查询
            shop = getById(id);
            log.debug("shop: {}", shop);
            // 4. 如果数据库中也不存在, 那么为了防止缓存缓存穿透, 采用空键值对的形式
            if (shop == null){
                redisClient.hset(key, null);
                redisClient.expire(key, Duration.ofMinutes(NULL_EXPIRE_TIME));
                log.debug("查询不到店铺 - id: {}", id);
                return Response.fail(SHOP_INFO_BLANK);
            }
            // 5. 如果数据库中存在, 那么就放入缓存后返回
            redisClient.hset(key, BeanUtil.beanToMap(shop, new HashMap<>(),
                    CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor(
                            (fieldName, fieldValue)-> fieldValue.toString())));
            // 6. 设置超时时间减轻缓存压力
            redisClient.expire(key, Duration.ofDays(SHOP_INFO_EXPIRE_TIME));
        } catch (InterruptedException e) {
            log.error("interrupted exception", e);
        } finally {
            redisClient.del(lockKey);
        }
        return shop != null ? Response.ok(shop) : Response.fail("存在异常");
    }

    private Response checkRedis(Map<Object, Object> fields) {
        // 2.1 如果存在商铺信息, 那么直接返回
        if (MapUtil.isNotEmpty(fields)){
            Shop shop = BeanUtil.fillBeanWithMap(fields, new Shop(), false);
            log.debug("shop: {}", shop);
            return Response.ok(shop);
        }
        // 2.2 如果为 empty 证明是之前放在里面的空值, 用于防止缓存穿透的
        if(fields == null)
            return Response.fail(SHOP_INFO_IS_NULL);
        return null;
    }

    @Override
    @Transactional
    public Response updateShopById(Shop shop) {
        if(shop == null || shop.getId() <= 0)
            return Response.fail(SHOP_INFO_IS_NULL);
        // 1. 更新数据库
        boolean isSuccess = updateById(shop);
        if(!isSuccess)
            return Response.fail(SHOP_INFO_UPDATE_FAILED);
        // 2. 删除缓存
        redisClient.del(CACHE_SHOP_KEY + shop.getId());
        return Response.ok();
    }
}
