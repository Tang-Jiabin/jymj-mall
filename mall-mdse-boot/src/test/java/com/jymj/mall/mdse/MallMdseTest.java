package com.jymj.mall.mdse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author 唐嘉彬
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-30
 */

@SpringBootTest
class MallMdseTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;



}
