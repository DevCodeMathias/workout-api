package com.devBackend.workout_api.Domain.Exception;

import org.springframework.http.HttpStatus;

public class ActivityNotFoundException extends ApplicationException {
    public ActivityNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "ACTIVITY_NOT_FOUND", "Activity not found with id: " + id);
    }
}
