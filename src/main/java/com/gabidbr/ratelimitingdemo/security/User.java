package com.gabidbr.ratelimitingdemo.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class User {
    private final String username;
    private final String password; // hashed
    private final List<GrantedAuthority> authorities;
}
