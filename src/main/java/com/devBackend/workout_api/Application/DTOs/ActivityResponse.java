package com.devBackend.workout_api.Application.DTOs;

import java.time.LocalDateTime;

public record ActivityResponse(
        String id,
        String employeeId,
        LocalDateTime activityDateTime,
        String activityCode,
        String activityDescription
) {
}
