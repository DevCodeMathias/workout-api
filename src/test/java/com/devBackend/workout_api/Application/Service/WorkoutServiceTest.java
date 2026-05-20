package com.devBackend.workout_api.Application.Service;

import com.devBackend.workout_api.Application.DTOs.ActivityRequest;
import com.devBackend.workout_api.Application.DTOs.ActivityResponse;
import com.devBackend.workout_api.Domain.Entity.Activity;
import com.devBackend.workout_api.Domain.Exception.ActivityNotFoundException;
import com.devBackend.workout_api.Domain.Repository.ActivityRepository;
import com.devBackend.workout_api.Infrastructure.Model.Envelope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {
    @Mock
    private ActivityRepository activityRepository;

    private WorkoutService workoutService;

    @BeforeEach
    void setUp() {
        workoutService = new WorkoutService(activityRepository);
    }

    @Test
    void createActivityShouldSaveActivityAndReturnResponse() {
        ActivityRequest request = new ActivityRequest("BICEPS", "Biceps workout");

        Activity savedActivity = new Activity(
                "employee-1",
                LocalDateTime.of(2026, 5, 19, 10, 0),
                "BICEPS",
                "Biceps workout"
        );
        Envelope<Activity> savedEnvelope = new Envelope<>(
                "activity-1",
                LocalDateTime.of(2026, 5, 19, 10, 1),
                LocalDateTime.of(2026, 5, 19, 10, 1),
                savedActivity
        );

        when(activityRepository.save(any(Activity.class))).thenReturn(savedEnvelope);

        ActivityResponse response = workoutService.createActivity("employee-1", request);

        assertEquals("activity-1", response.id());
        assertEquals("employee-1", response.employeeId());
        assertEquals("BICEPS", response.activityCode());
        assertEquals("Biceps workout", response.activityDescription());
        assertEquals(savedActivity.getActivityDateTime(), response.activityDateTime());

        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository).save(activityCaptor.capture());

        Activity activityToSave = activityCaptor.getValue();
        assertEquals("employee-1", activityToSave.getEmployeeId());
        assertEquals("BICEPS", activityToSave.getActivityCode());
        assertEquals("Biceps workout", activityToSave.getActivityDescription());
        assertNotNull(activityToSave.getActivityDateTime());
    }

    @Test
    void searchActivityByIdShouldReturnResponseWhenActivityExists() {
        Envelope<Activity> envelope = createEnvelope("activity-1", "employee-1");
        when(activityRepository.findById("activity-1")).thenReturn(Optional.of(envelope));

        ActivityResponse response = workoutService.searchActivityById("activity-1");

        assertEquals("activity-1", response.id());
        assertEquals("employee-1", response.employeeId());
        assertEquals("BICEPS", response.activityCode());
        assertEquals("Biceps workout", response.activityDescription());
    }

    @Test
    void searchActivityByIdShouldThrowWhenActivityDoesNotExist() {
        when(activityRepository.findById("missing-id")).thenReturn(Optional.empty());

        ActivityNotFoundException exception = assertThrows(
                ActivityNotFoundException.class,
                () -> workoutService.searchActivityById("missing-id")
        );

        assertEquals("Activity not found with id: missing-id", exception.getMessage());
    }

    @Test
    void searchActivityByEmployeeIdShouldReturnResponses() {
        when(activityRepository.findByEmployeeId("employee-1")).thenReturn(List.of(
                createEnvelope("activity-1", "employee-1"),
                createEnvelope("activity-2", "employee-1")
        ));

        List<ActivityResponse> response = workoutService.searchActivityByEmployeeId("employee-1");

        assertEquals(2, response.size());
        assertEquals("activity-1", response.get(0).id());
        assertEquals("activity-2", response.get(1).id());
        assertEquals("employee-1", response.get(0).employeeId());
        assertEquals("employee-1", response.get(1).employeeId());
    }

    private Envelope<Activity> createEnvelope(String activityId, String employeeId) {
        Activity activity = new Activity(
                employeeId,
                LocalDateTime.of(2026, 5, 19, 10, 0),
                "BICEPS",
                "Biceps workout"
        );

        return new Envelope<>(
                activityId,
                LocalDateTime.of(2026, 5, 19, 10, 1),
                LocalDateTime.of(2026, 5, 19, 10, 2),
                activity
        );
    }
}
