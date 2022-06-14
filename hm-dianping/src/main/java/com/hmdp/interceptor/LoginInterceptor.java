package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.hmdp.dto.UserResponse;
import com.hmdp.entity.User;
import com.hmdp.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.hmdp.constant.KeyConstant.USER_KEY;
import static com.hmdp.constant.ResponseCode.*;

/**
 * <h3>登陆拦截器</h3>
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 取出 session 中的用户
        Object attribute = request.getSession().getAttribute(USER_KEY);
        log.info("attribute: {}", attribute);
        // 2. 如果用户不存在就拦截
        if (attribute == null){
            response.setStatus(UNAUTHORIZED.getCode());
            return false;
        }
        // 3. 如果用户存在就存储在 ThreadLocal 中
        UserHolder.set(
                BeanUtil.copyProperties(
                        BeanUtil.toBean(attribute, User.class), UserResponse.class));
        response.setStatus(SUCCESS.getCode());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
