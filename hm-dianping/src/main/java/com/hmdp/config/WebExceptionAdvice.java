package com.hmdp.config;

import com.hmdp.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public Response handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return Response.fail("服务器异常");
    }
}
