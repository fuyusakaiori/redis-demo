package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.cache.RedisClient;
import com.hmdp.dto.UserResponse;
import com.hmdp.util.UserHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.util.Map;

import static com.hmdp.constant.KeyConstant.LOGIN_USER_KEY;
import static com.hmdp.constant.TimeConstant.TOKEN_EXPIRE_TIME;

/**
 * <h3>令牌刷新拦截器</h3>
 */
@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

    @Resource
    private RedisClient redisClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        // 1. 如果令牌为空, 那么直接进入下一个拦截器
        if (StrUtil.isBlank(token))
            return true;
        // 2. 如果缓存中没有对应的 token, 那么也直接进入下一个拦截器
        String key = LOGIN_USER_KEY + token;
        Map<Object, Object> fields = redisClient.hgetAll(key);
        if (MapUtil.isEmpty(fields))
            return true;
        // 3. 存入 redis 用于相应的拦截器判断
        UserResponse user = BeanUtil.fillBeanWithMap(fields, new UserResponse(), false);
        UserHolder.set(user);
        // 4. 刷新令牌时间
        redisClient.expire(key, Duration.ofMinutes(TOKEN_EXPIRE_TIME));
        return true;
    }
}
