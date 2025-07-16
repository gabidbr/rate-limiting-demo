package com.gabidbr.ratelimitingdemo.security;

import com.gabidbr.ratelimitingdemo.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final CustomUserDetailsService userDetailsService;

    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userDetailsService.getUser(username);
    }
}

