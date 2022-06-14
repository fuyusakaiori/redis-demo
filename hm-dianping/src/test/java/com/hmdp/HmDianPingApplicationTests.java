package com.hmdp;

import com.hmdp.cache.RedisClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Map;

import static com.hmdp.constant.KeyConstant.LOGIN_USER_KEY;

@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private RedisClient redisClient;

    @Test
    public void redisClientTest(){
        Map<Object, Object> fields = redisClient.hgetAll(LOGIN_USER_KEY + "q1trtg0l6j4i80s5yr6ys5sy1m");
        System.out.println(fields.size());
    }

}
