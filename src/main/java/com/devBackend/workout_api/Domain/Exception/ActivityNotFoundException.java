package com.devBackend.workout_api.Domain.Exception;

public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException(String id) {
        super("Activity not found with id: " + id);
    }
}
