package com.devbackend.workoutapi.controller;

import com.devbackend.workoutapi.application.dto.ActivityRequest;
import com.devbackend.workoutapi.application.dto.ActivityResponse;
import com.devbackend.workoutapi.application.port.WorkoutUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/activities")
public class WorkoutController {


    private final WorkoutUseCase workoutService;

    public WorkoutController(WorkoutUseCase workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/healthCheck")
    public String healthCheck() {
        return "running";
    }

    @GetMapping("/me")
    public List<ActivityResponse> searchAuthenticatedEmployeeActivities(
            @AuthenticationPrincipal String employeeId
    ) {
        return workoutService.searchActivityByEmployeeId(employeeId);
    }

    @GetMapping
    public List<ActivityResponse> searchAllActivities(
            @AuthenticationPrincipal String employeeId
    ) {
        return workoutService.searchAllActivities(employeeId);
    }

    @GetMapping("/{id}")
    public ActivityResponse searchActivityById(
            @PathVariable String id,
            @AuthenticationPrincipal String employeeId
    ) {
        return workoutService.searchActivityById(id, employeeId);
    }

    @PostMapping
    public ActivityResponse createActivity(
            @RequestBody ActivityRequest payload,
            @AuthenticationPrincipal String employeeId
    ) {
        return workoutService.createActivity(employeeId, payload);
    }
}
