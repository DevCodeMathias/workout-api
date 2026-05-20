package com.devBackend.workout_api.Application.DTOs;

public record ActivityRequest(
        String activityCode,
        String activityDescription
) {
}
