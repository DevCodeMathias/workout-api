package com.devbackend.workoutapi.application.service;

import com.devbackend.workoutapi.application.dto.ActivityRequest;
import com.devbackend.workoutapi.application.dto.ActivityResponse;
import com.devbackend.workoutapi.application.interfaces.WorkoutUseCase;
import com.devbackend.workoutapi.domain.entity.Activity;
import com.devbackend.workoutapi.domain.exception.ActivityNotFoundException;
import com.devbackend.workoutapi.domain.exception.EmployeeNotFoundException;
import com.devbackend.workoutapi.domain.repository.ActivityRepository;
import com.devbackend.workoutapi.domain.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class WorkoutService implements WorkoutUseCase {
    private static final Logger logger = LoggerFactory.getLogger(WorkoutService.class);
    private final ActivityRepository activityRepository;
    private final EmployeeRepository employeeRepository;

    public WorkoutService(ActivityRepository activityRepository, EmployeeRepository employeeRepository) {
        this.activityRepository = activityRepository;
        this.employeeRepository = employeeRepository;
    }


    @Override
    public List<ActivityResponse> searchAllActivities(String authenticatedEmployeeId) {
        validateEmployeeExists(authenticatedEmployeeId);

        logger.info("Searching all activities");
        return activityRepository.findAll().stream()
                .map(this::toActivityResponse)
                .toList();
    }

    
    @Override
    public ActivityResponse createActivity(String employeeId, ActivityRequest payload) {

        validateEmployeeExists(employeeId);

        logger.info("Saving activity for employee id: {}", employeeId);

        Activity activity = new Activity(
                employeeId,
                LocalDateTime.now(),
                payload.activityCode(),
                payload.activityDescription()
        );

        Activity savedActivity = activityRepository.save(activity);
        return toActivityResponse(savedActivity);
    }

    @Override
    public List<ActivityResponse> searchActivityByEmployeeId(String employeeId) {
        logger.info("Searching activities by employee id: {}", employeeId);
        validateEmployeeExists(employeeId);

        List<Activity> activities = activityRepository.findByEmployeeId(employeeId);

        return activities.stream()
                .map(this::toActivityResponse)
                .toList();

    }

    @Override
    public ActivityResponse searchActivityById(String id, String authenticatedEmployeeId) {
        validateEmployeeExists(authenticatedEmployeeId);

        logger.info("Searching activity by id: {}", id);
        return activityRepository.findById(id)
                .filter(activity -> Objects.equals(activity.getEmployeeId(), authenticatedEmployeeId))
                .map(this::toActivityResponse)
                .orElseThrow(() -> new ActivityNotFoundException(id));
    }

    private void validateEmployeeExists(String employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }
    }

    private ActivityResponse toActivityResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getEmployeeId(),
                activity.getActivityDateTime(),
                activity.getActivityCode(),
                activity.getActivityDescription()
        );
    }
}
