package com.hmdp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;

    public static Response ok(){
        return new Response(true, null, null, null);
    }
    public static Response ok(Object data){
        return new Response(true, null, data, null);
    }
    public static Response ok(List<?> data, Long total){
        return new Response(true, null, data, total);
    }
    public static Response fail(String errorMsg){
        return new Response(false, errorMsg, null, null);
    }
}
