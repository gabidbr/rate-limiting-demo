package com.gabidbr.ratelimitingdemo.security.filters;

import com.gabidbr.ratelimitingdemo.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000L;

    private final Map<String, Deque<Long>> requestTimestampsUsername = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String username = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtils.isValidToken(token)) {
                    username = jwtUtils.extractUsername(token);
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Invalid token!");
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid token!");
                return;
            }
        }

        if (username != null) {
            long now = System.currentTimeMillis();
            requestTimestampsUsername.putIfAbsent(username, new ConcurrentLinkedDeque<>());

            Deque<Long> timestamps = requestTimestampsUsername.get(username);
            synchronized (timestamps) {
                // Clean up timestamps older than one minute
                while (!timestamps.isEmpty() && now - timestamps.peekFirst() > ONE_MINUTE_IN_MILLIS) {
                    timestamps.pollFirst();
                }

                if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.getWriter().write("Too many requests! Try again in a minute.");
                    return;
                }

                // Add current timestamp
                timestamps.addLast(now);
            }
        }
        filterChain.doFilter(request, response);
    }
}
