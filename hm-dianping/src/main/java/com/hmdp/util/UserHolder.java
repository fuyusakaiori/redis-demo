package com.hmdp.util;

import com.hmdp.dto.UserResponse;

public class UserHolder {
    private static final ThreadLocal<UserResponse> threadLocal = new ThreadLocal<>();

    public static void set(UserResponse user){
        threadLocal.set(user);
    }

    public static UserResponse get(){
        return threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }
}
