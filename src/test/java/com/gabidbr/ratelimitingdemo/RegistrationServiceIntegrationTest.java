package com.gabidbr.ratelimitingdemo;

import com.gabidbr.ratelimitingdemo.security.RegistrationService;
import com.gabidbr.ratelimitingdemo.security.UserLoginRepository;
import com.gabidbr.ratelimitingdemo.security.UserRepository;
import com.gabidbr.ratelimitingdemo.security.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class RegistrationServiceIntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @BeforeEach
    void setup() {
        userLoginRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldAllowOnlyOneConcurrentRegistration() throws InterruptedException {
        String username = "concurrentUser";
        String password = "secret";

        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(5);
        List<String> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    registrationService.register(username, password);
                    results.add("SUCCESS");
                } catch (UserAlreadyExistsException e) {
                    results.add("ALREADY_EXISTS");
                } catch (Exception e) {
                    results.add("ERROR");
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        long success = results.stream().filter(r -> r.equals("SUCCESS")).count();
        long duplicates = results.stream().filter(r -> r.equals("ALREADY_EXISTS")).count();

        System.out.println("Results: " + results);

        assertEquals(1, success, "Only one thread should register the user.");
        assertEquals(4, duplicates, "Other threads should see 'user already exists'.");
    }
}

