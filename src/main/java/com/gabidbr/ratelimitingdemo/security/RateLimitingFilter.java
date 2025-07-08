package com.gabidbr.ratelimitingdemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000L;

    private final Map<String, Deque<Long>> requestTimestampsPerIp = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get the client's IP address
        String clientIp = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        //initialize the count if not existing for user
        requestTimestampsPerIp.putIfAbsent(clientIp, new ConcurrentLinkedDeque<>());
        Deque<Long> timestamps = requestTimestampsPerIp.get(clientIp);
        // clean up timestamps longer than a minute
        while(!timestamps.isEmpty() && now - timestamps.peekFirst() > ONE_MINUTE_IN_MILLIS) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests! Try again in a minute.");
            return;
        }

        // Add current timestamp and proceed
        timestamps.addLast(now);
        filterChain.doFilter(request, response);
    }
}
