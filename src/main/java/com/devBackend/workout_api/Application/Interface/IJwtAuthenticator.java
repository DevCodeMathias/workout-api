package com.devBackend.workout_api.Application.Interface;

public interface IJwtAuthenticator {
    void authenticate(String authorizationHeader);

    void authenticateEmployee(String authorizationHeader, String employeeId);

    String getAuthenticatedEmployeeId(String authorizationHeader);
}
