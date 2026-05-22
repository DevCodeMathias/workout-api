package com.devbackend.workoutapi.infrastructure.security;

import com.devbackend.workoutapi.domain.exception.AuthenticationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtAuthenticationTest {
    private static final String SECRET = "test-secret-key-with-at-least-32-characters";
    private static final String OTHER_SECRET = "other-secret-key-with-at-least-32-characters";

    private JwtAuthentication jwtAuthenticationService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret(SECRET);
        jwtProperties.setExpirationHours(24);

        jwtAuthenticationService = new JwtAuthentication(jwtProperties);
    }

    @Test
    void getAuthenticatedEmployeeIdShouldThrowWhenAuthorizationHeaderIsMissing() {
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.getAuthenticatedEmployeeId(null)
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    @Test
    void getAuthenticatedEmployeeIdShouldThrowWhenAuthorizationHeaderDoesNotStartWithBearer() {
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.getAuthenticatedEmployeeId("Token abc")
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    @Test
    void getAuthenticatedEmployeeIdShouldThrowWhenJwtFormatIsInvalid() {
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer invalid-token")
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    @Test
    void getAuthenticatedEmployeeIdShouldThrowWhenJwtSignatureIsInvalid() {
        String token = createToken("employee-1", false, OTHER_SECRET);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer " + token)
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    @Test
    void getAuthenticatedEmployeeIdShouldThrowWhenJwtIsExpired() {
        String token = createToken("employee-1", true, SECRET);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer " + token)
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
    }

    @Test
    void getAuthenticatedEmployeeIdShouldThrowWhenAuthorizationTypeIsInvalid() {
        String token = createTokenWithAuthorizationType("employee-1", "ADMIN", false, SECRET);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer " + token)
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
        assertEquals("JWT authorization type is invalid", exception.getMessage());
    }

    @Test
    void getAuthenticatedEmployeeIdShouldAcceptValidSignedJwt() {
        String token = createToken("employee-1", false, SECRET);

        assertDoesNotThrow(() -> jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer " + token));
    }

    @Test
    void getAuthenticatedEmployeeIdShouldReturnEmployeeIdFromToken() {
        String token = createToken("employee-1", false, SECRET);

        String employeeId = jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer " + token);

        assertEquals("employee-1", employeeId);
    }

    @Test
    void getAuthenticatedEmployeeIdShouldThrowWhenEmployeeIdIsMissing() {
        String token = createTokenWithoutEmployeeId();

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> jwtAuthenticationService.getAuthenticatedEmployeeId("Bearer " + token)
        );

        assertEquals("INVALID_TOKEN", exception.getCode());
        assertEquals("JWT does not contain employeeId", exception.getMessage());
    }

    private String createToken(String employeeId, boolean expired, String secret) {
        return createTokenWithAuthorizationType(employeeId, "EMPLOYEE", expired, secret);
    }

    private String createTokenWithAuthorizationType(
            String employeeId,
            String authorizationType,
            boolean expired,
            String secret
    ) {
        Instant now = Instant.now();
        Instant expiration = expired ? now.minusSeconds(60) : now.plusSeconds(3600);

        return Jwts.builder()
                .claim("employeeId", employeeId)
                .claim("authorizationType", authorizationType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(createSecretKey(secret))
                .compact();
    }

    private String createTokenWithoutEmployeeId() {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject("employee-1")
                .claim("authorizationType", "EMPLOYEE")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(createSecretKey(SECRET))
                .compact();
    }

    private SecretKey createSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
