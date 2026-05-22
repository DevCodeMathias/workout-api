package com.devbackend.workoutapi.application.service;

import com.devbackend.workoutapi.application.dto.ActivityRequest;
import com.devbackend.workoutapi.application.dto.ActivityResponse;
import com.devbackend.workoutapi.domain.entity.Activity;
import com.devbackend.workoutapi.domain.exception.ActivityNotFoundException;
import com.devbackend.workoutapi.domain.exception.EmployeeNotFoundException;
import com.devbackend.workoutapi.domain.repository.ActivityRepository;
import com.devbackend.workoutapi.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {
    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private WorkoutService workoutService;

    @BeforeEach
    void setUp() {
        workoutService = new WorkoutService(activityRepository, employeeRepository);
    }

    @Test
    void createActivityShouldSaveActivityAndReturnResponse() {
        ActivityRequest request = new ActivityRequest("BICEPS", "Biceps workout");

        Activity savedActivity = new Activity(
                "activity-1",
                "employee-1",
                LocalDateTime.of(2026, 5, 19, 10, 0),
                "BICEPS",
                "Biceps workout"
        );

        when(employeeRepository.existsById("employee-1")).thenReturn(true);
        when(activityRepository.save(any(Activity.class))).thenReturn(savedActivity);

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
        Activity activity = createActivity("activity-1", "employee-1");
        when(employeeRepository.existsById("employee-1")).thenReturn(true);
        when(activityRepository.findById("activity-1")).thenReturn(Optional.of(activity));

        ActivityResponse response = workoutService.searchActivityById("activity-1", "employee-1");

        assertEquals("activity-1", response.id());
        assertEquals("employee-1", response.employeeId());
        assertEquals("BICEPS", response.activityCode());
        assertEquals("Biceps workout", response.activityDescription());
    }

    @Test
    void searchAllActivitiesShouldReturnResponses() {
        when(employeeRepository.existsById("employee-1")).thenReturn(true);
        when(activityRepository.findAll()).thenReturn(List.of(
                createActivity("activity-1", "employee-1"),
                createActivity("activity-2", "employee-2")
        ));

        List<ActivityResponse> response = workoutService.searchAllActivities("employee-1");

        assertEquals(2, response.size());
        assertEquals("activity-1", response.get(0).id());
        assertEquals("activity-2", response.get(1).id());
        assertEquals("employee-1", response.get(0).employeeId());
        assertEquals("employee-2", response.get(1).employeeId());
    }

    @Test
    void searchActivityByIdShouldThrowWhenActivityDoesNotExist() {
        when(employeeRepository.existsById("employee-1")).thenReturn(true);
        when(activityRepository.findById("missing-id")).thenReturn(Optional.empty());

        ActivityNotFoundException exception = assertThrows(
                ActivityNotFoundException.class,
                () -> workoutService.searchActivityById("missing-id", "employee-1")
        );

        assertEquals("Activity not found with id: missing-id", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("ACTIVITY_NOT_FOUND", exception.getCode());
    }

    @Test
    void searchActivityByIdShouldThrowWhenActivityBelongsToAnotherEmployee() {
        Activity activity = createActivity("activity-1", "employee-2");
        when(employeeRepository.existsById("employee-1")).thenReturn(true);
        when(activityRepository.findById("activity-1")).thenReturn(Optional.of(activity));

        ActivityNotFoundException exception = assertThrows(
                ActivityNotFoundException.class,
                () -> workoutService.searchActivityById("activity-1", "employee-1")
        );

        assertEquals("Activity not found with id: activity-1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("ACTIVITY_NOT_FOUND", exception.getCode());
    }

    @Test
    void searchActivityByEmployeeIdShouldReturnResponses() {
        when(employeeRepository.existsById("employee-1")).thenReturn(true);
        when(activityRepository.findByEmployeeId("employee-1")).thenReturn(List.of(
                createActivity("activity-1", "employee-1"),
                createActivity("activity-2", "employee-1")
        ));

        List<ActivityResponse> response = workoutService.searchActivityByEmployeeId("employee-1");

        assertEquals(2, response.size());
        assertEquals("activity-1", response.get(0).id());
        assertEquals("activity-2", response.get(1).id());
        assertEquals("employee-1", response.get(0).employeeId());
        assertEquals("employee-1", response.get(1).employeeId());
    }

    @Test
    void searchActivityByEmployeeIdShouldThrowWhenEmployeeDoesNotExist() {
        when(employeeRepository.existsById("missing-employee")).thenReturn(false);

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> workoutService.searchActivityByEmployeeId("missing-employee")
        );

        assertEquals("Employee not found with id: missing-employee", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("EMPLOYEE_NOT_FOUND", exception.getCode());
    }

    @Test
    void searchAllActivitiesShouldThrowWhenAuthenticatedEmployeeDoesNotExist() {
        when(employeeRepository.existsById("missing-employee")).thenReturn(false);

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> workoutService.searchAllActivities("missing-employee")
        );

        assertEquals("Employee not found with id: missing-employee", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("EMPLOYEE_NOT_FOUND", exception.getCode());
        verify(activityRepository, never()).findAll();
    }

    @Test
    void searchActivityByIdShouldThrowWhenAuthenticatedEmployeeDoesNotExist() {
        when(employeeRepository.existsById("missing-employee")).thenReturn(false);

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> workoutService.searchActivityById("activity-1", "missing-employee")
        );

        assertEquals("Employee not found with id: missing-employee", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("EMPLOYEE_NOT_FOUND", exception.getCode());
        verify(activityRepository, never()).findById("activity-1");
    }

    @Test
    void createActivityShouldThrowWhenEmployeeDoesNotExist() {
        ActivityRequest request = new ActivityRequest("BICEPS", "Biceps workout");
        when(employeeRepository.existsById("missing-employee")).thenReturn(false);

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> workoutService.createActivity("missing-employee", request)
        );

        assertEquals("Employee not found with id: missing-employee", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("EMPLOYEE_NOT_FOUND", exception.getCode());
        verify(activityRepository, never()).save(any(Activity.class));
    }

    private Activity createActivity(String activityId, String employeeId) {
        return new Activity(
                activityId,
                employeeId,
                LocalDateTime.of(2026, 5, 19, 10, 0),
                "BICEPS",
                "Biceps workout"
        );
    }
}
