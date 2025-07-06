package com.sky.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdUtil {
    private static final long BEGIN_TIMESTAMP = 1640995200L;

    private static int COUNT_BITS = 32;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Long nextId(String keyPrefix){
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long time = nowSecond - BEGIN_TIMESTAMP;

        String format = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        // Redis Incrby 命令将 key 中储存的数字加上指定的增量值。
        // 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。
        Long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + format);

        return time << COUNT_BITS | count;
    }
}
