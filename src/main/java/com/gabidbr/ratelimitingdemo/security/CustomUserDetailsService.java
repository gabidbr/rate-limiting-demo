package com.gabidbr.ratelimitingdemo.security;

import com.gabidbr.ratelimitingdemo.security.entity.User;
import com.gabidbr.ratelimitingdemo.security.entity.UserLogin;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserLoginRepository userLoginRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void createUser(String username, String rawPassword) {
        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .build();
        userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public void addLogin(User user) {
        var userLogin = UserLogin.builder()
                .user(user)
                .loginTimestamp(java.time.Instant.now())
                .build();
        userLoginRepository.save(userLogin);
    }
}

