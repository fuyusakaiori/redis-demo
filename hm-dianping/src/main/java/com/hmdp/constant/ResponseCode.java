package com.hmdp.constant;

public enum ResponseCode {
    UNAUTHORIZED(401, "用户没有权限"), SUCCESS(200, "成功");

    // 响应状态码
    private int code;
    // 响应状态码描述
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
