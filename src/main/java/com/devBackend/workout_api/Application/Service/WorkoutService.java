package com.devBackend.workout_api.Application.Service;

import com.devBackend.workout_api.Application.DTOs.ActivityRequest;
import com.devBackend.workout_api.Application.DTOs.ActivityResponse;
import com.devBackend.workout_api.Application.Interface.IWorkoutService;
import com.devBackend.workout_api.Domain.Entity.Activity;
import com.devBackend.workout_api.Domain.Exception.ActivityNotFoundException;
import com.devBackend.workout_api.Domain.Exception.AuthenticationException;
import com.devBackend.workout_api.Domain.Exception.EmployeeNotFoundException;
import com.devBackend.workout_api.Domain.Repository.ActivityRepository;
import com.devBackend.workout_api.Domain.Repository.EmployeeRepository;
import com.devBackend.workout_api.Infrastructure.Model.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkoutService implements IWorkoutService {
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

        Envelope<Activity> savedActivity = activityRepository.save(activity);
        return toActivityResponse(savedActivity);
    }

    @Override
    public List<ActivityResponse> searchActivityByEmployeeId(String employeeId) {
        logger.info("Searching activities by employee id: {}", employeeId);
        validateEmployeeExists(employeeId);

        List<Envelope<Activity>> activities = activityRepository.findByEmployeeId(employeeId);

        return activities.stream()
                .map(this::toActivityResponse)
                .toList();

    }

    @Override
    public ActivityResponse searchActivityById(String id, String authenticatedEmployeeId) {
        validateEmployeeExists(authenticatedEmployeeId);

        logger.info("Searching activity by id: {}", id);
        Envelope<Activity> activity = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));

        validateActivityBelongsToEmployee(activity, authenticatedEmployeeId);

        return toActivityResponse(activity);
    }

    private void validateEmployeeExists(String employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }
    }

    private void validateActivityBelongsToEmployee(Envelope<Activity> activity, String authenticatedEmployeeId) {
        String activityEmployeeId = activity.getBody().getEmployeeId();

        if (!authenticatedEmployeeId.equals(activityEmployeeId)) {
            throw new AuthenticationException(
                    "TOKEN_EMPLOYEE_MISMATCH",
                    "JWT employee does not match activity employee"
            );
        }
    }

    private ActivityResponse toActivityResponse(Envelope<Activity> envelope) {
        Activity activity = envelope.getBody();

        return new ActivityResponse(
                envelope.getId(),
                activity.getEmployeeId(),
                activity.getActivityDateTime(),
                activity.getActivityCode(),
                activity.getActivityDescription()
        );
    }
}
