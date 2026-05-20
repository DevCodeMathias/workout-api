package com.devBackend.workout_api.Controller;

import com.devBackend.workout_api.Application.DTOs.ActivityRequest;
import com.devBackend.workout_api.Application.DTOs.ActivityResponse;
import com.devBackend.workout_api.Application.Interface.IWorkoutService;
import com.devBackend.workout_api.Infrastructure.Auth.JwtAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutControllerTest {
    private static final String AUTHORIZATION_HEADER = "Bearer token";

    @Mock
    private IWorkoutService workoutService;

    @Mock
    private JwtAuthenticator jwtAuthenticator;

    private WorkoutController workoutController;

    @BeforeEach
    void setUp() {
        workoutController = new WorkoutController(workoutService, jwtAuthenticator);
    }

    @Test
    void healthCheckShouldReturnRunning() {
        String response = workoutController.healthCheck();

        assertEquals("running", response);
    }

    @Test
    void searchAuthenticatedEmployeeActivitiesShouldGetEmployeeFromJwtAndReturnActivities() {
        List<ActivityResponse> expectedResponse = List.of(createActivityResponse("activity-1", "employee-1"));
        when(jwtAuthenticator.getAuthenticatedEmployeeId(AUTHORIZATION_HEADER)).thenReturn("employee-1");
        when(workoutService.searchActivityByEmployeeId("employee-1")).thenReturn(expectedResponse);

        List<ActivityResponse> response = workoutController.searchAuthenticatedEmployeeActivities(
                AUTHORIZATION_HEADER
        );

        assertEquals(expectedResponse, response);
        verify(jwtAuthenticator).getAuthenticatedEmployeeId(AUTHORIZATION_HEADER);
        verify(workoutService).searchActivityByEmployeeId("employee-1");
    }

    @Test
    void searchActivityByIdShouldAuthenticateRequestAndReturnActivity() {
        ActivityResponse expectedResponse = createActivityResponse("activity-1", "employee-1");
        when(workoutService.searchActivityById("activity-1")).thenReturn(expectedResponse);

        ActivityResponse response = workoutController.searchActivityById("activity-1", AUTHORIZATION_HEADER);

        assertEquals(expectedResponse, response);
        verify(jwtAuthenticator).authenticate(AUTHORIZATION_HEADER);
        verify(workoutService).searchActivityById("activity-1");
    }

    @Test
    void createActivityShouldAuthenticateEmployeeAndReturnCreatedActivity() {
        ActivityRequest request = new ActivityRequest("RUN", "Morning run");
        ActivityResponse expectedResponse = createActivityResponse("activity-1", "employee-1");
        when(jwtAuthenticator.getAuthenticatedEmployeeId(AUTHORIZATION_HEADER)).thenReturn("employee-1");
        when(workoutService.createActivity("employee-1", request)).thenReturn(expectedResponse);

        ActivityResponse response = workoutController.createActivity(request, AUTHORIZATION_HEADER);

        assertEquals(expectedResponse, response);
        verify(jwtAuthenticator).getAuthenticatedEmployeeId(AUTHORIZATION_HEADER);
        verify(workoutService).createActivity("employee-1", request);
    }

    private ActivityResponse createActivityResponse(String activityId, String employeeId) {
        return new ActivityResponse(
                activityId,
                employeeId,
                LocalDateTime.of(2026, 5, 20, 10, 0),
                "RUN",
                "Morning run"
        );
    }
}
