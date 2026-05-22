package com.devbackend.workoutapi.controller;

import com.devbackend.workoutapi.application.dto.ActivityRequest;
import com.devbackend.workoutapi.application.dto.ActivityResponse;
import com.devbackend.workoutapi.application.port.WorkoutUseCase;
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
    @Mock
    private WorkoutUseCase workoutService;

    private WorkoutController workoutController;

    @BeforeEach
    void setUp() {
        workoutController = new WorkoutController(workoutService);
    }

    @Test
    void healthCheckShouldReturnRunning() {
        String response = workoutController.healthCheck();

        assertEquals("running", response);
    }

    @Test
    void searchAuthenticatedEmployeeActivitiesShouldGetEmployeeFromAuthenticationAndReturnActivities() {
        List<ActivityResponse> expectedResponse = List.of(createActivityResponse("activity-1", "employee-1"));
        when(workoutService.searchActivityByEmployeeId("employee-1")).thenReturn(expectedResponse);

        List<ActivityResponse> response = workoutController.searchAuthenticatedEmployeeActivities(
                "employee-1"
        );

        assertEquals(expectedResponse, response);
        verify(workoutService).searchActivityByEmployeeId("employee-1");
    }

    @Test
    void searchAllActivitiesShouldPassAuthenticatedEmployeeToServiceAndReturnActivities() {
        List<ActivityResponse> expectedResponse = List.of(
                createActivityResponse("activity-1", "employee-1"),
                createActivityResponse("activity-2", "employee-2")
        );
        when(workoutService.searchAllActivities("employee-1")).thenReturn(expectedResponse);

        List<ActivityResponse> response = workoutController.searchAllActivities("employee-1");

        assertEquals(expectedResponse, response);
        verify(workoutService).searchAllActivities("employee-1");
    }

    @Test
    void searchActivityByIdShouldPassAuthenticatedEmployeeToServiceAndReturnActivity() {
        ActivityResponse expectedResponse = createActivityResponse("activity-1", "employee-1");
        when(workoutService.searchActivityById("activity-1", "employee-1")).thenReturn(expectedResponse);

        ActivityResponse response = workoutController.searchActivityById(
                "activity-1",
                "employee-1"
        );

        assertEquals(expectedResponse, response);
        verify(workoutService).searchActivityById("activity-1", "employee-1");
    }

    @Test
    void createActivityShouldUseAuthenticatedEmployeeAndReturnCreatedActivity() {
        ActivityRequest request = new ActivityRequest("RUN", "Morning run");
        ActivityResponse expectedResponse = createActivityResponse("activity-1", "employee-1");
        when(workoutService.createActivity("employee-1", request)).thenReturn(expectedResponse);

        ActivityResponse response = workoutController.createActivity(request, "employee-1");

        assertEquals(expectedResponse, response);
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
