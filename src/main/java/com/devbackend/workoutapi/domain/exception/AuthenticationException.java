package com.devbackend.workoutapi.domain.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
