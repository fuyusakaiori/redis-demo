package com.hmdp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static com.hmdp.constant.ExpressionConstant.redisClientLogExpressions;

@Slf4j
@Aspect
@Component
public class RedisClientAopAdvice {

    @Pointcut(redisClientLogExpressions)
    public void log(){

    }


    @Before("log()")
    public void beforeAdvice(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        log.info("=============================================>>>>");
        for (Object arg : args) {
            log.info("{}: {}", arg.getClass().getSimpleName(), arg);
        }
        log.info("<<<<=============================================");
    }

}
