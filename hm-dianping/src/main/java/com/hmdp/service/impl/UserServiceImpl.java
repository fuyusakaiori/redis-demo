package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.cache.RedisClient;
import com.hmdp.constant.TimeConstant;
import com.hmdp.dto.LoginMessage;
import com.hmdp.dto.Response;
import com.hmdp.dto.UserResponse;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static com.hmdp.constant.KeyConstant.*;
import static com.hmdp.constant.MessageConstant.*;
import static com.hmdp.constant.TimeConstant.*;

/**
 * 《<h3>用户服务</h3>
 *
 * @author 冬坂五百里
 * @since 2022-06-14
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private RedisClient redisClient;

    @Override
    public Response sendCode(String phone, HttpSession session) {
        // 1. 校验手机号: 正则表达式
        if (RegexUtil.checkPhone(phone))
            // 注: 如果手机号为空, 那么就直接返回
            return Response.fail(PHONE_NOT_BLANK);
        // 2. 生成验证码: hutool
        String code = RandomUtil.randomNumbers(6);
        // 3. 保存验证码在 Redis: 采用 string 存储
        redisClient.set(CODE_KEY + phone, code, Duration.ofMinutes(CODE_EXPIRE_TIME));
        // 4. 发送验证码: 需要第三方服务的支持, 这里就不调用了, 直接使用日志进行记录
        log.info("生成验证码: {}", code);
        return Response.ok();
    }

    @Override
    @Transactional
    public Response login(LoginMessage login, HttpSession session) {
        // 1. 检验手机号: 正则表达式
        if (RegexUtil.checkPhone(login.getPhone()))
            return Response.fail(PHONE_NOT_BLANK);
        // 2. 获取验证码并进行校验: redis
        String code = redisClient.get(CODE_KEY + login.getPhone());
        if (!StrUtil.equals(login.getCode(), code))
            return Response.fail(CODE_ERROR);
        // 3. 查询用户
        User user = query().eq("phone", login.getPhone()).one();
        // 4. 如果不存在就直接注册
        if (user == null)
            user = register(login);
        // 5. 转换类型: 减少 redis 缓存压力
        UserResponse response =
                BeanUtil.copyProperties(user, UserResponse.class);
        // 5. 存入 Redis: hash
        // 注: 登陆请求肯定不会携带用户 id, 所以不要用 user:id 的形式做 key, 而是采用随机 token 的形式
        String token = RandomUtil.randomString(26);
        String key = LOGIN_USER_KEY + token;
        redisClient.hset(key, BeanUtil.beanToMap(response, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString())));
        redisClient.expire(key, Duration.ofMinutes(TOKEN_EXPIRE_TIME));
        return Response.ok(token);
    }

    private User register(LoginMessage login) {
        User user = new User();
        user.setPhone(login.getPhone());
        user.setNickName(USER_NICKNAME_PREFIX + RandomUtil.randomString(10));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        save(user);
        return user;
    }
}
