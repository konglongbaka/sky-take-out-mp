package com.sky.utils;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisLockUtil implements ILock {


    private StringRedisTemplate stringRedisTemplate;
    private String name;
    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final String UUID_PREFIX = UUID.randomUUID().toString();
    public RedisLockUtil(StringRedisTemplate stringRedisTemplate, String name) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.name = name;
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        String key = LOCK_KEY_PREFIX + name;
        String value = Thread.currentThread().getName()+UUID_PREFIX;
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(isLock);
    }

    @Override
    public void unlock() {
        String value = Thread.currentThread().getName()+UUID_PREFIX;
        if (stringRedisTemplate.opsForValue().get(LOCK_KEY_PREFIX + name).equals(value)) {
            stringRedisTemplate.delete(LOCK_KEY_PREFIX + name);
        }
    }
}
