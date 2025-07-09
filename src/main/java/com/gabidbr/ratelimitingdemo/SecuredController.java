package com.gabidbr.ratelimitingdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SecuredController {

    private final RedisTestService redisTestService;

    /**
     * This endpoint is public and can be accessed without authentication.
     * It is used to test the Redis service.
     */
    @GetMapping("/public/hello")
    public String publicHello() {
        log.info("Redis test service is being called");
        redisTestService.testRedis();
        return "Hello from a public endpoint!";
    }

    /**
     * This endpoint is secured and requires authentication.
     * It returns a message indicating that it is a secured endpoint.
     */
    @GetMapping("/secure/hello")
    public String secureHello() {
        return "Hello from a secured endpoint!";
    }
}
