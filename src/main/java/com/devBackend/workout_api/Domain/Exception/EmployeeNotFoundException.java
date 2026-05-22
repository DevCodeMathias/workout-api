package com.devBackend.workout_api.Domain.Exception;

import org.springframework.http.HttpStatus;

public class EmployeeNotFoundException extends ApplicationException {
    public EmployeeNotFoundException(String id) {
        super(HttpStatus.UNAUTHORIZED, "EMPLOYEE_NOT_FOUND", "Employee not found with id: " + id);
    }
}
