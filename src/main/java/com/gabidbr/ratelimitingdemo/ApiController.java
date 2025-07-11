package com.gabidbr.ratelimitingdemo;

import com.gabidbr.ratelimitingdemo.security.CustomUserDetailsService;
import com.gabidbr.ratelimitingdemo.security.JwtUtils;
import com.gabidbr.ratelimitingdemo.security.RegistrationService;
import com.gabidbr.ratelimitingdemo.security.exception.UserAlreadyExistsException;
import com.gabidbr.ratelimitingdemo.security.dto.LoginRequest;
import com.gabidbr.ratelimitingdemo.security.dto.RegisterRequest;
import com.gabidbr.ratelimitingdemo.security.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class ApiController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        log.info("User {} authenticated successfully", loginRequest.getUsername());
        String token = jwtUtils.generateToken(authentication.getName());
        try {
            userDetailsService.addLogin(userDetailsService.getUser(loginRequest.getUsername()));
            log.info("Saved login for user {}", loginRequest.getUsername());
        } catch (Exception e) {
            log.error("Failed to save login for user {}: {}", loginRequest.getUsername(), e.getMessage());
        }
        return new TokenResponse(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try{
            registrationService.register(registerRequest.username(), registerRequest.password());
        }catch (UserAlreadyExistsException e) {
            log.warn("User {} already exists", registerRequest.username());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
