package com.devbackend.workoutapi.infrastructure.repository;

import com.devbackend.workoutapi.domain.entity.Activity;
import com.devbackend.workoutapi.domain.repository.ActivityRepository;
import com.devbackend.workoutapi.infrastructure.model.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MongoActivityRepository implements ActivityRepository {
    private static final Logger logger = LoggerFactory.getLogger(MongoActivityRepository.class);

    private final SpringDataActivityMongoRepository mongoRepository;

    public MongoActivityRepository(SpringDataActivityMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Envelope<Activity> save(Activity activity) {
        logger.debug("Saving activity document for employee id: {}", activity.getEmployeeId());
        Envelope<Activity> savedActivity = mongoRepository.save(Envelope.of(activity));
        logger.debug("Activity document saved id={}", savedActivity.getId());
        return savedActivity;
    }

    @Override
    public Optional<Envelope<Activity>> findById(String id) {
        logger.debug("Finding activity document by id: {}", id);
        Optional<Envelope<Activity>> activity = mongoRepository.findById(id);
        logger.debug("Activity document lookup result id={} found={}", id, activity.isPresent());
        return activity;
    }

    @Override
    public List<Envelope<Activity>> findByEmployeeId(String employeeId) {
        logger.debug("Finding activity documents by employee id: {}", employeeId);
        List<Envelope<Activity>> activities = mongoRepository.findByEmployeeId(employeeId);
        logger.debug("Activity documents found employeeId={} count={}", employeeId, activities.size());
        return activities;
    }

    @Override
    public List<Envelope<Activity>> findAll() {
        logger.debug("Finding all activity documents");
        List<Envelope<Activity>> activities = mongoRepository.findAll();
        logger.debug("All activity documents found count={}", activities.size());
        return activities;
    }
}
