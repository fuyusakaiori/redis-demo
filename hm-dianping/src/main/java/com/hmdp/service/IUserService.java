package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginMessage;
import com.hmdp.dto.Response;
import com.hmdp.entity.User;

import javax.servlet.http.HttpSession;

/**
 * @author 冬坂五百里
 * @since 2022-06-12
 */
public interface IUserService extends IService<User> {

    Response sendCode(String phone, HttpSession session);
    Response login(LoginMessage loginBody, HttpSession session);
}
