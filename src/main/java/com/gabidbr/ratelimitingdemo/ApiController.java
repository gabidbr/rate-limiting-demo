package com.gabidbr.ratelimitingdemo;

import com.gabidbr.ratelimitingdemo.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        String token = jwtUtils.generateToken(authentication.getName());
        return new TokenResponse(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        if(userDetailsService.userExists(registerRequest.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        userDetailsService.createUser(registerRequest.username(), registerRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
