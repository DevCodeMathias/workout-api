package com.devbackend.workoutapi.infrastructure.repository;

import com.devbackend.workoutapi.domain.entity.Activity;
import com.devbackend.workoutapi.infrastructure.model.Envelope;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SpringDataActivityMongoRepository extends MongoRepository<Envelope<Activity>, String> {
    @Query("{ 'body.employeeId': ?0 }")
    List<Envelope<Activity>> findByEmployeeId(String employeeId);
}
