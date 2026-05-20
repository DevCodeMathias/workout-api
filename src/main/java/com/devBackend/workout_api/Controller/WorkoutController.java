package com.devBackend.workout_api.Controller;

import com.devBackend.workout_api.Application.DTOs.ActivityRequest;
import com.devBackend.workout_api.Application.DTOs.ActivityResponse;
import com.devBackend.workout_api.Application.Interface.IWorkoutService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class WorkoutController {

    private final IWorkoutService workoutService;

    public WorkoutController(IWorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/healthCheck")
    public String healthCheck() {
        return "running";
    }

    @GetMapping("/activities/employees/{employeeId}")
    public List<ActivityResponse> searchActivityByEmployeeId(@PathVariable String employeeId) {
        return workoutService.searchActivityByEmployeeId(employeeId);
    }

    @GetMapping("/activities/{id}")
    public ActivityResponse searchActivityById(@PathVariable String id) {
        return workoutService.searchActivityById(id);
    }

    @PostMapping("/activities/{employeeId}")
    public ActivityResponse createActivity(@PathVariable String employeeId, @RequestBody ActivityRequest payload) {
        return workoutService.createActivity(employeeId, payload);
    }
}
