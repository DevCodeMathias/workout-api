package com.devbackend.workoutapi.application.dto;

public record ActivityRequest(
        String activityCode,
        String activityDescription
) {
}
