package com.devbackend.workoutapi.application.interfaces;

import com.devbackend.workoutapi.application.dto.ActivityRequest;
import com.devbackend.workoutapi.application.dto.ActivityResponse;


import java.util.List;

public interface WorkoutUseCase {
    List<ActivityResponse> searchAllActivities(String authenticatedEmployeeId);

    List<ActivityResponse> searchActivityByEmployeeId(String employeeId);

    ActivityResponse searchActivityById(String id, String authenticatedEmployeeId);

    ActivityResponse createActivity(String employeeId, ActivityRequest payload);
}
