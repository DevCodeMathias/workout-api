package com.devBackend.workout_api.Controller;

import com.devBackend.workout_api.Application.DTOs.ActivityRequest;
import com.devBackend.workout_api.Application.DTOs.ActivityResponse;
import com.devBackend.workout_api.Application.Interface.IWorkoutService;
import com.devBackend.workout_api.Application.Interface.IJwtAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/activities")
public class WorkoutController {
    private static final Logger logger = LoggerFactory.getLogger(WorkoutController.class);

    private final IWorkoutService workoutService;
    private final IJwtAuthenticator jwtAuthenticator;

    public WorkoutController(IWorkoutService workoutService, IJwtAuthenticator jwtAuthenticator) {
        this.workoutService = workoutService;
        this.jwtAuthenticator = jwtAuthenticator;
    }

    @GetMapping("/healthCheck")
    public String healthCheck() {
        logger.info("Health check requested");
        return "running";
    }

    @GetMapping("/me")
    public List<ActivityResponse> searchAuthenticatedEmployeeActivities(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        String employeeId = jwtAuthenticator.getAuthenticatedEmployeeId(authorizationHeader);
        logger.info("Request received to search activities by employee id: {}", employeeId);
        return workoutService.searchActivityByEmployeeId(employeeId);
    }

    @GetMapping
    public List<ActivityResponse> searchAllActivities(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        logger.info("Request received to search all activities");
        jwtAuthenticator.authenticate(authorizationHeader);
        return workoutService.searchAllActivities();
    }

    @GetMapping("/{id}")
    public ActivityResponse searchActivityById(
            @PathVariable String id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        logger.info("Request received to search activity by id: {}", id);
        jwtAuthenticator.authenticate(authorizationHeader);
        return workoutService.searchActivityById(id);
    }

    @PostMapping
    public ActivityResponse createActivity(
            @RequestBody ActivityRequest payload,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        String employeeId = jwtAuthenticator.getAuthenticatedEmployeeId(authorizationHeader);
        logger.info("Request received to create activity for employee id: {}", employeeId);
        return workoutService.createActivity(employeeId, payload);
    }
}
