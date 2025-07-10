package com.gabidbr.ratelimitingdemo;

import org.junit.jupiter.api.*;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RedissonLockIntegrationTest {

    static final GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    private RedissonClient redissonClient;

    @BeforeAll
    void setUp() {
        redis.start();
        String address = "redis://" + redis.getHost() + ":" + redis.getMappedPort(6379);

        Config config = new Config();
        config.useSingleServer().setAddress(address);
        redissonClient = Redisson.create(config);
    }

    @AfterAll
    void tearDown() {
        redissonClient.shutdown();
        redis.stop();
    }

    @Test
    void testDistributedLockWithConcurrency() throws InterruptedException {
        String lockKey = "user:register:testuser";
        int threads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1); // Make threads start together
        CountDownLatch doneLatch = new CountDownLatch(threads);
        List<String> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // All threads wait here
                    RLock lock = redissonClient.getLock(lockKey);
                    if (lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                        try {
                            // Simulate longer DB processing (so others time out)
                            Thread.sleep(2000);
                            results.add("SUCCESS");
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        results.add("LOCK_FAILED");
                    }
                } catch (Exception e) {
                    results.add("ERROR: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Let all threads run!
        doneLatch.await(); // Wait for all threads
        executor.shutdown();

        long success = results.stream().filter(r -> r.equals("SUCCESS")).count();
        long failures = results.stream().filter(r -> r.equals("LOCK_FAILED")).count();

        System.out.println("Results: " + results);
        Assertions.assertEquals(1, success, "Only one thread should acquire the lock.");
        Assertions.assertTrue(failures >= 4, "Other threads should fail to acquire the lock.");
    }

}
