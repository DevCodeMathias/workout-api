package com.devBackend.workout_api.Infrastructure.Security;

import com.devBackend.workout_api.Domain.Exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtAuthenticationServiceTest {
    private JwtAuthentication jwtAuthenticationService;

    @BeforeEach
    void setUp() {
        jwtAuthenticationService = new JwtAuthentication();
    }

    @Test
    void authenticateShouldThrowWhenAuthorizationHeaderIsMissing() {
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.authenticate(null)
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    @Test
    void authenticateShouldThrowWhenAuthorizationHeaderIsNotJwt() {
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.authenticate("Token abc")
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    @Test
    void authenticateShouldThrowWhenJwtFormatIsInvalid() {
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.authenticate("Bearer invalid-token")
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    @Test
    void authenticateShouldAcceptValidJwtFormat() {
        String token = createToken("{\"sub\":\"employee-1\"}");

        assertDoesNotThrow(() -> jwtAuthenticationService.authenticate("Bearer " + token));
    }

    @Test
    void authenticateEmployeeShouldAcceptWhenTokenEmployeeMatchesPathEmployee() {
        String token = createToken("{\"employeeId\":\"employee-1\"}");

        assertDoesNotThrow(() -> jwtAuthenticationService.authenticateEmployee("Bearer " + token, "employee-1"));
    }

    @Test
    void getAuthenticatedEmployeeIdShouldReturnEmployeeIdFromToken() {
        String token = createToken("{\"employeeId\":\"employee-1\"}");

        String employeeId = jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer " + token);

        assertEquals("employee-1", employeeId);
    }

    @Test
    void getAuthenticatedEmployeeIdShouldThrowWhenEmployeeIdIsMissing() {
        String token = createToken("{\"sub\":\"employee-1\"}");

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer " + token)
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
        assertEquals("JWT does not contain employeeId", exception.getMessage());
    }

    @Test
    void authenticateEmployeeShouldThrowWhenTokenEmployeeDoesNotMatchPathEmployee() {
        String token = createToken("{\"employeeId\":\"employee-1\"}");

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.authenticateEmployee("Bearer " + token, "employee-2")
        );

        assertEquals("TOKEN_EMPLOYEE_MISMATCH", exception.getCode());
    }

    @Test
    void authenticateEmployeeShouldThrowWhenTokenDoesNotContainEmployeeIdentifier() {
        String token = createToken("{\"name\":\"Laura\"}");

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.authenticateEmployee("Bearer " + token, "employee-1")
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    private String createToken(String payload) {
        return encode("{\"alg\":\"none\"}") + "." + encode(payload) + "." + encode("signature");
    }

    private String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
