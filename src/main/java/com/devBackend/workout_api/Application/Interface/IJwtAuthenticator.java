package com.devBackend.workout_api.Application.Interface;

public interface IJwtAuthenticator {
    String getAuthenticatedEmployeeId(String authorizationHeader);
}
