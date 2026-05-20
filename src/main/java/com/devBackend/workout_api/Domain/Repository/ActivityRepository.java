package com.devBackend.workout_api.Domain.Repository;

import com.devBackend.workout_api.Domain.Entity.Activity;
import com.devBackend.workout_api.Infrastructure.Model.Envelope;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    Envelope<Activity> save(Activity activity);

    Optional<Envelope<Activity>> findById(String id);

    List<Envelope<Activity>> findByEmployeeId(String employeeId);
}
