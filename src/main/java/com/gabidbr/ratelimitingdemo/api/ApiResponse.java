package com.gabidbr.ratelimitingdemo.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;
    private String message;
    private int status;
    private Instant timestamp;

    public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .status(status.value())
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .data(null)
                .message(message)
                .status(status.value())
                .timestamp(Instant.now())
                .build();
    }
}
