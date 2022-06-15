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

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

import static com.hmdp.constant.KeyConstant.CACHE_SHOP_KEY;
import static com.hmdp.constant.MessageConstant.SHOP_INFO_BLANK;

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

    @Override
    public Response getShopById(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 1. 查询 Redis
        Map<Object, Object> fields = redisClient.hgetAll(key);
        // 2. 如果存在商铺信息, 那么直接返回
        if (MapUtil.isNotEmpty(fields)){
            Shop shop = BeanUtil.fillBeanWithMap(fields, new Shop(), false);
            log.debug("shop: {}", shop);
            return Response.ok(shop);
        }
        // 3. 如果不存在商铺信息, 那么就去数据库中查询
        Shop shop = getById(id);
        log.debug("shop: {}", shop);
        // 4. 如果数据库中也不存在, 那么直接返回
        if (shop == null){
            log.debug("查询不到店铺 - id: {}", id);
            return Response.fail(SHOP_INFO_BLANK);
        }
        // 5. 如果数据库中存在, 那么就放入缓存后返回
        redisClient.hset(key, BeanUtil.beanToMap(shop, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor(
                        (fieldName, fieldValue)-> fieldValue.toString())));
        return Response.ok(shop);
    }
}
