package com.hmdp.constant;

public class KeyConstant {
    // 验证码缓存 key
    public static final String CODE_KEY = "phone:";

    public static final String USER_NICKNAME_PREFIX = "bilibili";
    // 令牌缓存 key
    public static final String LOGIN_USER_KEY = "token:";
    // 商铺缓存 key
    public static final String CACHE_SHOP_KEY = "cache:shop:info";
    // 商铺类型缓存 key
    public static final String CACHE_SHOP_TYPE_KEY = "cache:shop:type";
    // 分布式锁 Key
    public static final String LOCK_CACHE_SHOP_INFO_KEY = "lock:shop:info:key";
    // 分布式锁 Value
    public static final String LOCK_CACHE_SHOP_VALUE = "lock_shop_info_value";

}
