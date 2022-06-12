package com.hmdp.dto;

import lombok.Data;

@Data
public class LoginMessage
{
    private String phone;
    private String code;
    private String password;
}
