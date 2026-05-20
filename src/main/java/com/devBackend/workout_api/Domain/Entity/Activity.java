package com.devBackend.workout_api.Domain.Entity;

import java.time.LocalDateTime;

public class Activity {
    private String employeeId;
    private LocalDateTime activityDateTime;
    private String activityCode;
    private String activityDescription;

    public Activity() {
    }

    public Activity(String employeeId, LocalDateTime activityDateTime, String activityCode, String activityDescription) {
        this.employeeId = employeeId;
        this.activityDateTime = activityDateTime;
        this.activityCode = activityCode;
        this.activityDescription = activityDescription;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getActivityDateTime() {
        return activityDateTime;
    }

    public void setActivityDateTime(LocalDateTime activityDateTime) {
        this.activityDateTime = activityDateTime;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }
}
