package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.cache.RedisClient;
import com.hmdp.dto.UserResponse;
import com.hmdp.entity.User;
import com.hmdp.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static com.hmdp.constant.KeyConstant.LOGIN_USER_KEY;
import static com.hmdp.constant.ResponseCode.*;

/**
 * <h3>登陆拦截器</h3>
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisClient redisClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取 token
        String token = request.getHeader("authorization");
        log.debug("authorization token: {}", token);
        if(StrUtil.isBlank(token)){
            response.setStatus(UNAUTHORIZED.getCode());
            return false;
        }
        // 2. 取出 redis 中的用户
        Map<Object, Object> fields = redisClient.hgetAll(LOGIN_USER_KEY + token);
        // 3. 如果用户不存在就拦截
        if (MapUtil.isEmpty(fields)) {
            response.setStatus(UNAUTHORIZED.getCode());
            return false;
        }
        // 4. 如果用户存在就存储在 ThreadLocal 中
        UserResponse user = BeanUtil.fillBeanWithMap(fields, new UserResponse(), false);
        UserHolder.set(user);
        response.setStatus(SUCCESS.getCode());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}
