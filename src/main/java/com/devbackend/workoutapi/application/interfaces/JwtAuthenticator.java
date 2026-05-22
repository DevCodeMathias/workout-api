package com.devbackend.workoutapi.application.interfaces;

public interface JwtAuthenticator {
    String getAuthenticatedEmployeeId(String authorizationHeader);
}
