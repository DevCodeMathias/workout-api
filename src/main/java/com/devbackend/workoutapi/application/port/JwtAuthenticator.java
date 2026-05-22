package com.devbackend.workoutapi.application.port;

public interface JwtAuthenticator {
    String getAuthenticatedEmployeeId(String authorizationHeader);
}
