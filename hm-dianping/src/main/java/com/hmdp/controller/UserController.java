package com.hmdp.controller;


import com.hmdp.dto.LoginMessage;
import com.hmdp.dto.Response;
import com.hmdp.entity.UserInfo;
import com.hmdp.service.IUserInfoService;
import com.hmdp.service.IUserService;
import com.hmdp.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * <h3>用户登录</h3>
 *
 * @author 冬坂五百里
 * @since 2022-06-12
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * <h3>发送手机验证码</h3>
     */
    @PostMapping("/code")
    public Response sendCode(@RequestParam("phone") String phone, HttpSession session) {
        return userService.sendCode(phone, session);
    }

    /**
     * <h3>用户登录: 记录用户的信息</h3>
     * @param login 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Response login(@RequestBody LoginMessage login, HttpSession session){
        return userService.login(login, session);
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Response logout(){
        // TODO 实现登出功能
        return Response.fail("功能未完成");
    }

    /**
     * <h3>跳转到登陆页</h3>
     */
    @GetMapping("/me")
    public Response me(){
        return Response.ok(UserHolder.get());
    }

    @GetMapping("/info/{id}")
    public Response info(@PathVariable("id") Long userId){
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Response.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Response.ok(info);
    }
}
