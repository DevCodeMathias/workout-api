package com.devbackend.workoutapi.domain.repository;

import com.devbackend.workoutapi.domain.entity.Activity;
import com.devbackend.workoutapi.infrastructure.model.Envelope;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    Envelope<Activity> save(Activity activity);

    Optional<Envelope<Activity>> findById(String id);

    List<Envelope<Activity>> findByEmployeeId(String employeeId);

    List<Envelope<Activity>> findAll();
}
