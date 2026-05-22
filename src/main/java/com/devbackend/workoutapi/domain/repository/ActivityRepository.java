package com.devbackend.workoutapi.domain.repository;

import com.devbackend.workoutapi.domain.entity.Activity;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    Activity save(Activity activity);

    Optional<Activity> findById(String id);

    List<Activity> findByEmployeeId(String employeeId);

    List<Activity> findAll();
}
