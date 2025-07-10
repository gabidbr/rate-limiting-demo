package com.gabidbr.ratelimitingdemo;

import com.gabidbr.ratelimitingdemo.security.exception.InternalServiceException;
import com.gabidbr.ratelimitingdemo.security.exception.LockAcquisitionException;
import com.gabidbr.ratelimitingdemo.security.exception.UserAlreadyExistsException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LockAcquisitionException.class)
    public ResponseEntity<String> handleLockException(LockAcquisitionException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
    }

    @ExceptionHandler(InternalServiceException.class)
    public ResponseEntity<String> handleInternalError(InternalServiceException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
    }
}

