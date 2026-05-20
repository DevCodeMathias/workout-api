package com.devBackend.workout_api.Infrastructure.Repository;

import com.devBackend.workout_api.Domain.Entity.Activity;
import com.devBackend.workout_api.Infrastructure.Model.Envelope;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SpringDataActivityMongoRepository extends MongoRepository<Envelope<Activity>, String> {
    @Query("{ 'body.employeeId': ?0 }")
    List<Envelope<Activity>> findByEmployeeId(String employeeId);
}
