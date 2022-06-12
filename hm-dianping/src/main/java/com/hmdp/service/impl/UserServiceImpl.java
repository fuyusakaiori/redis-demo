package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginMessage;
import com.hmdp.dto.Response;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;

import static com.hmdp.util.KeyConstant.*;
import static com.hmdp.util.MessageConstant.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Response sendCode(String phone, HttpSession session) {
        // 1. 校验手机号: 正则表达式
        if (RegexUtil.checkPhone(phone))
            // 注: 如果手机号为空, 那么就直接返回
            return Response.fail(PHONE_NOT_BLANK);
        // 2. 生成验证码: hutool
        String code = RandomUtil.randomNumbers(6);
        // 3. 保存验证码在 session: 登录的时候需要进行比对
        session.setAttribute(CODE_KEY,  code);
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
        // 2. 获取验证码并进行校验: session
        String code = String.valueOf(session.getAttribute(CODE_KEY));
        if (!StrUtil.equals(login.getCode(), code))
            return Response.fail(CODE_ERROR);
        // 3. 查询用户
        User user = query().eq("phone", login.getPhone()).one();
        // 4. 如果不存在就直接注册
        if (user == null)
            user = register(login);
        // 5. 存入 session / ThreadLocal
        session.setAttribute(USER_KEY, user);
        return Response.ok();
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
