package com.gabidbr.ratelimitingdemo.security;

import com.gabidbr.ratelimitingdemo.configuration.RedisLockService;
import com.gabidbr.ratelimitingdemo.security.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RedisLockService redisLockService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    public void register(String username, String password) {
        redisLockService.runWithLock("user:register:" + username, 5, 10, () -> {
            if (userRepository.existsByUsername(username)) {
                throw new UserAlreadyExistsException(username);
            }
            userDetailsService.createUser(username, password);
        });
    }
}
