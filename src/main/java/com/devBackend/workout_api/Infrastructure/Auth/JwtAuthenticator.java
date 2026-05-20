package com.devBackend.workout_api.Infrastructure.Auth;

public interface JwtAuthenticator {
    void authenticate(String authorizationHeader);

    void authenticateEmployee(String authorizationHeader, String employeeId);

    String getAuthenticatedEmployeeId(String authorizationHeader);
}
