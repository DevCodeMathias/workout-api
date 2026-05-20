package com.devBackend.workout_api.Domain.Exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
