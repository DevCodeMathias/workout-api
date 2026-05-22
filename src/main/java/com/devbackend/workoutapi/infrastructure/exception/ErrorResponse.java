package com.devbackend.workoutapi.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        Map<String, String> fields
) {
}
