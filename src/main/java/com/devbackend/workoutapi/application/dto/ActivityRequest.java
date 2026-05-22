package com.devbackend.workoutapi.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ActivityRequest(
        @NotBlank(message = "activityCode is required")
        String activityCode,

        @NotBlank(message = "activityDescription is required")
        String activityDescription
) {
}
