package com.sky.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissionConfig {
    public static final String REDIS_CONNECTION_STRING = "redis://127.0.0.1:6379";
    public static final String REDIS_PASSWORD = "123456";
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDIS_CONNECTION_STRING);
        return Redisson.create(config);
    }
}
