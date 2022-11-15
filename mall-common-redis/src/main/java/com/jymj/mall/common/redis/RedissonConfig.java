package com.jymj.mall.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁 Redisson 配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redissonClient;
        Config config = new Config();
        //密码可以单独配置
        String url = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        config.useSingleServer().setAddress(url) //单机
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase())
                .setConnectTimeout(5000)
                .setTimeout(5000)
                .setConnectionPoolSize(64)
                .setConnectionMinimumIdleSize(10)
                .setPingConnectionInterval(10000);

        redissonClient = Redisson.create(config);
        return redissonClient;

    }

}
