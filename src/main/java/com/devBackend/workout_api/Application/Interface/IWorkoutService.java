package com.devBackend.workout_api.Application.Interface;

import com.devBackend.workout_api.Application.DTOs.ActivityRequest;
import com.devBackend.workout_api.Application.DTOs.ActivityResponse;


import java.util.List;

public interface IWorkoutService {
    List<ActivityResponse> searchActivityByEmployeeId(String employeeId);
    ActivityResponse searchActivityById(String id);
    ActivityResponse createActivity(String employeeId, ActivityRequest payload);
}
