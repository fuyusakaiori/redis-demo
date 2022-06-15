package com.hmdp.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.cache.RedisClient;
import com.hmdp.dto.Response;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;

import static com.hmdp.constant.KeyConstant.CACHE_SHOP_TYPE_KEY;
import static com.hmdp.constant.MessageConstant.SHOP_TYPE_LIST_BLANK;

/**
 * <h3>店铺类型服务类</h3>
 *
 * @author 冬坂五百里
 * @since 2022-06-15
 */
@Slf4j
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private RedisClient redisClient;

    /**
     * <h3>1. 如果对每个店铺采用 prefix + id 作为 key, 各个属性值作为 value 的 hash 结构</h3>
     * <h3>2. 那么访问缓存的时候就会存在问题, 因为访问的时候是无法提前预知 id 的, 所以 key 无法构造</h3>
     * <h3>3. 暂时的解决方案是这样的: 直接使用 prefix 作为 key, 商铺类型的字符串形式作为 value</h3>
     */
    @Override
    public Response getAllShop() {
        String prefix = CACHE_SHOP_TYPE_KEY;
        Map<Object, Object> typesStrMap  = redisClient.hgetAll(prefix);
        if (MapUtil.isNotEmpty(typesStrMap)){
            List<ShopType> types = new ArrayList<>(typesStrMap.size());
            typesStrMap.forEach((key, value) -> types.add(
                    JSONUtil.toBean(String.valueOf(value), ShopType.class)));
            return Response.ok(types, (long) types.size());
        }
        List<ShopType> types = query().orderByAsc("sort").list();
        if (types == null || types.isEmpty())
            return Response.fail(SHOP_TYPE_LIST_BLANK);
        Map<String, String> map = new HashMap<>();
        types.forEach(type -> {
            String json = JSONUtil.toJsonStr(type);
            log.debug("json: {}", json);
            map.put(type.getName(), json);
        });
        redisClient.hset(prefix, map);
        return Response.ok(types, (long) types.size());
    }
}
