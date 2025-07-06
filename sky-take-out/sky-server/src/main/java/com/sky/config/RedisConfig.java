package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>RedisConfig 此类用于：Redis相关配置，用于解决存入Redis中值乱码问题 </p>
 * <p>@author：hujm</p>
 * <p>@date：2022年08月18日 18:04</p>
 * <p>@remark：</p>
 */
@Slf4j
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        log.info("Redis的KeySerializer设置为：{}", StringRedisSerializer.class);
        // 默认的Key序列化器为：JdkSerializationRedisSerializer
        // 将key的序列化器改为StringRedisSerializer，以便可以在Redis的key设置什么就显示什么，不进行转化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }


}