package com.devbackend.workoutapi.infrastructure.exception;

import com.devbackend.workoutapi.domain.exception.EmployeeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/v1/activities/employees/employee-1");
    }

    @Test
    void handleApplicationExceptionShouldReturnStandardErrorResponse() {
        ResponseEntity<ErrorResponse> response = handler.handleApplicationException(
                new EmployeeNotFoundException("employee-1"),
                request
        );

        ErrorResponse body = response.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.timestamp());
        assertEquals(401, body.status());
        assertEquals("Unauthorized", body.error());
        assertEquals("EMPLOYEE_NOT_FOUND", body.code());
        assertEquals("Employee not found with id: employee-1", body.message());
        assertEquals("/v1/activities/employees/employee-1", body.path());
        assertNull(body.fields());
    }

    @Test
    void handleMessageNotReadableExceptionShouldReturnInvalidRequestBodyResponse() {
        ResponseEntity<ErrorResponse> response = handler.handleMessageNotReadableException(
                new HttpMessageNotReadableException("Invalid JSON", mock(HttpInputMessage.class)),
                request
        );

        ErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Bad Request", body.error());
        assertEquals("INVALID_REQUEST_BODY", body.code());
        assertEquals("Request body is invalid or malformed", body.message());
        assertEquals("/v1/activities/employees/employee-1", body.path());
        assertNull(body.fields());
    }

    @Test
    void handleMethodNotSupportedExceptionShouldReturnMethodNotAllowedResponse() {
        ResponseEntity<ErrorResponse> response = handler.handleMethodNotSupportedException(
                new HttpRequestMethodNotSupportedException("POST"),
                request
        );

        ErrorResponse body = response.getBody();

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(body);
        assertEquals(405, body.status());
        assertEquals("Method Not Allowed", body.error());
        assertEquals("METHOD_NOT_ALLOWED", body.code());
        assertEquals("Request method is not supported for this endpoint", body.message());
        assertEquals("/v1/activities/employees/employee-1", body.path());
        assertNull(body.fields());
    }

    @Test
    void handleUnexpectedExceptionShouldReturnInternalServerErrorWithoutTrace() {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpectedException(
                new RuntimeException("Database stack trace should not be exposed"),
                request
        );

        ErrorResponse body = response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(body);
        assertEquals(500, body.status());
        assertEquals("Internal Server Error", body.error());
        assertEquals("INTERNAL_SERVER_ERROR", body.code());
        assertEquals("Unexpected internal server error", body.message());
        assertEquals("/v1/activities/employees/employee-1", body.path());
        assertNull(body.fields());
    }
}
