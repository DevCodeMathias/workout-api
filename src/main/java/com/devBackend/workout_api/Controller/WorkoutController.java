package com.devBackend.workout_api.Controller;

import com.devBackend.workout_api.Application.DTOs.ActivityRequest;
import com.devBackend.workout_api.Application.DTOs.ActivityResponse;
import com.devBackend.workout_api.Application.Interface.IWorkoutService;
import com.devBackend.workout_api.Domain.Exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/activities")
public class WorkoutController {
    private static final Logger logger = LoggerFactory.getLogger(WorkoutController.class);

    private final IWorkoutService workoutService;

    public WorkoutController(IWorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/healthCheck")
    public String healthCheck() {
        logger.info("Health check requested");
        return "running";
    }

    @GetMapping("/me")
    public List<ActivityResponse> searchAuthenticatedEmployeeActivities(
            @AuthenticationPrincipal String employeeId
    ) {
        logger.info("Request received to search activities by employee id: {}", employeeId);
        return workoutService.searchActivityByEmployeeId(employeeId);
    }

    @GetMapping("/employees/{employeeId}")
    public List<ActivityResponse> searchEmployeeActivities(
            @PathVariable String employeeId,
            @AuthenticationPrincipal String authenticatedEmployeeId
    ) {
        if (!employeeId.equals(authenticatedEmployeeId)) {
            throw new AuthenticationException(
                    "TOKEN_EMPLOYEE_MISMATCH",
                    "JWT employee does not match requested employee"
            );
        }

        logger.info("Request received to search activities by employee id: {}", employeeId);
        return workoutService.searchActivityByEmployeeId(employeeId);
    }

    @GetMapping
    public List<ActivityResponse> searchAllActivities(
            @AuthenticationPrincipal String employeeId
    ) {
        logger.info("Request received to search all activities");
        return workoutService.searchAllActivities(employeeId);
    }

    @GetMapping("/{id}")
    public ActivityResponse searchActivityById(
            @PathVariable String id,
            @AuthenticationPrincipal String employeeId
    ) {
        logger.info("Request received to search activity by id: {}", id);
        return workoutService.searchActivityById(id, employeeId);
    }

    @PostMapping
    public ActivityResponse createActivity(
            @RequestBody ActivityRequest payload,
            @AuthenticationPrincipal String employeeId
    ) {
        logger.info("Request received to create activity for employee id: {}", employeeId);
        return workoutService.createActivity(employeeId, payload);
    }
}
