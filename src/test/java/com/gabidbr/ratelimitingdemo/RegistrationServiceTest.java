package com.gabidbr.ratelimitingdemo;

import com.gabidbr.ratelimitingdemo.configuration.RedisLockService;
import com.gabidbr.ratelimitingdemo.security.CustomUserDetailsService;
import com.gabidbr.ratelimitingdemo.security.RegistrationService;
import com.gabidbr.ratelimitingdemo.security.UserRepository;
import com.gabidbr.ratelimitingdemo.security.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private RedisLockService redisLockService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void shouldRegisterUserWhenUsernameNotExistsInDb() {
        String username = "testuser";
        String password = "password";

        when(userRepository.existsByUsername(username)).thenReturn(false);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(3);
            task.run(); // simulate the lock wrapper calling the task
            return null;
        }).when(redisLockService).runWithLock(eq("user:register:" + username), anyLong(), anyLong(), any());

        registrationService.register(username, password);

        verify(userRepository).existsByUsername(username);
        verify(userDetailsService).createUser(username, password);
    }

    @Test
    void shouldThrowExceptionWhenUsernameExists() {
        String username = "existinguser";
        String password = "password";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(3);
            task.run();
            return null;
        }).when(redisLockService).runWithLock(eq("user:register:" + username), anyLong(), anyLong(), any());

        assertThrows(UserAlreadyExistsException.class, () -> registrationService.register(username, password));

        verify(userRepository).existsByUsername(username);
        verify(userDetailsService, never()).createUser(anyString(), anyString());
    }
}
