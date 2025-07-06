package com.sky.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RedisTimeUtil {
    @Autowired
    private static StringRedisTemplate redisTemplate;

    public static void setRandomExpiration(String key, Integer min, Integer max , TimeUnit timeUnit){
        Random random =new Random();
        int expiration =random.nextInt(max-min +1)+ min;
        redisTemplate.expire(key,expiration, timeUnit);
    }

    public static void setRandomExpiration(String key,TimeUnit timeUnit){
        Random random =new Random();
        int expiration =random.nextInt(11)+ 1;
        redisTemplate.expire(key,expiration, timeUnit);
    }
}
