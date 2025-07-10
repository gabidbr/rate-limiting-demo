package com.gabidbr.ratelimitingdemo.configuration;

import com.gabidbr.ratelimitingdemo.security.exception.InternalServiceException;
import com.gabidbr.ratelimitingdemo.security.exception.LockAcquisitionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {
    private final RedissonClient redissonClient;

    public RedisLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void runWithLock(String key, long waitSec, long leaseSec, Runnable task) {
        RLock lock = redissonClient.getLock(key);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(waitSec, leaseSec, TimeUnit.SECONDS);
            if (!acquired) {
                throw new LockAcquisitionException("Could not acquire lock for key: " + key);
            }
            task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalServiceException("Lock interrupted", e);
        } finally {
            if (acquired) lock.unlock();
        }
    }
}

