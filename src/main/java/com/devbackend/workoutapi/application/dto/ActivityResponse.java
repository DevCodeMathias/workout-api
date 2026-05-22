package com.devbackend.workoutapi.application.dto;

import java.time.LocalDateTime;

public record ActivityResponse(
        String id,
        String employeeId,
        LocalDateTime activityDateTime,
        String activityCode,
        String activityDescription
) {
}
