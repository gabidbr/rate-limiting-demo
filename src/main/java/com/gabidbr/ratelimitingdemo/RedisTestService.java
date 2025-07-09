package com.gabidbr.ratelimitingdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTestService {
    private final RedisTemplate<String, String> redisTemplate;

    public void testRedis() {
        // Set a value in Redis
        redisTemplate.opsForValue().set("testKey", "testValue");
        log.info("Value set in Redis: " + redisTemplate.opsForValue().get("testKey"));
    }
}
