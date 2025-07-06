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

    /**
     * 缓存管理
     *
     * @return 返回缓存管理信息
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 缓存配置
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                // 默认没有特殊指定的缓存，设置失效时间为1天
                .entryTtl(Duration.ofDays(1))
                // 在缓存名称前加上前缀
                .computePrefixWith(cacheName -> "default:" + cacheName);
        log.info("设置redis缓存的默认失效时间，失效时间默认为：{}天", defaultCacheConfig.getTtl().toDays());
        // 针对不同cacheName，设置不同的失效时间，map的key是缓存名称（注解设定的value/cacheNames），value是缓存的失效配置
        Map<String, RedisCacheConfiguration> initialCacheConfiguration = new HashMap<String, RedisCacheConfiguration>(8);
        // 设定失效时间为1小时
        initialCacheConfiguration.put("userCache", getDefaultSimpleConfiguration().entryTtl(Duration.ofMillis(50000)));
        // 设定失效时间为10分钟
        initialCacheConfiguration.put("userCache1", getDefaultSimpleConfiguration().entryTtl(Duration.ofMinutes(10)));
        // 设定失效时间为12小时
        initialCacheConfiguration.put("userCache2", getDefaultSimpleConfiguration().entryTtl(Duration.ofHours(12)));
        // ...如果有其他的不同cacheName需要控制失效时间，以此类推即可进行添加
        return RedisCacheManager.builder(redisConnectionFactory)
                // 设置缓存默认失效时间配置，也就是动态或者未指定的缓存将会使用当前配置
                .cacheDefaults(defaultCacheConfig)
                // 不同不同cacheName的个性化配置
                .withInitialCacheConfigurations(initialCacheConfiguration).build();
    }

    /**
     * 覆盖默认的构造key[默认拼接的时候是两个冒号（::）]，否则会多出一个冒号
     *
     * @return 返回缓存配置信息
     */
    private RedisCacheConfiguration getDefaultSimpleConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheName -> cacheName + ":");
    }



}