package com.devBackend.workout_api.Infrastructure.Repository;

import com.devBackend.workout_api.Domain.Entity.Activity;
import com.devBackend.workout_api.Domain.Repository.ActivityRepository;
import com.devBackend.workout_api.Infrastructure.Model.Envelope;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MongoActivityRepository implements ActivityRepository {
    private final SpringDataActivityMongoRepository mongoRepository;

    public MongoActivityRepository(SpringDataActivityMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Envelope<Activity> save(Activity activity) {
        return mongoRepository.save(Envelope.of(activity));
    }

    @Override
    public Optional<Envelope<Activity>> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public List<Envelope<Activity>> findByEmployeeId(String employeeId) {
        return mongoRepository.findByEmployeeId(employeeId);
    }
}
