package com.gabidbr.ratelimitingdemo.security.exception;

public class InternalServiceException extends RuntimeException {
    public InternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
