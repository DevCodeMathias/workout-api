package com.devbackend.workoutapi.infrastructure.security;

import com.devbackend.workoutapi.application.interfaces.JwtAuthenticator;
import com.devbackend.workoutapi.domain.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtAuthentication implements JwtAuthenticator {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String EMPLOYEE_ID_CLAIM = "employeeId";
    private static final String AUTHORIZATION_TYPE_CLAIM = "authorizationType";
    private static final String EMPLOYEE_AUTHORIZATION_TYPE = "EMPLOYEE";

    private final SecretKey secretKey;

    public JwtAuthentication(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String getAuthenticatedEmployeeId(String authorizationHeader) {
        Claims claims = getClaims(authorizationHeader);

        validateAuthorizationType(claims);

        String employeeId = claims.get(EMPLOYEE_ID_CLAIM, String.class);

        if (employeeId == null || employeeId.isBlank()) {
            throw new AuthenticationException(
                    "INVALID_TOKEN",
                    "JWT does not contain employeeId"
            );
        }

        return employeeId;
    }

    private Claims getClaims(String authorizationHeader) {
        String token = extractToken(authorizationHeader);

        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException | IllegalArgumentException exception) {
            throw new AuthenticationException(
                    "INVALID_TOKEN",
                    "JWT is invalid"
            );
        }
    }

    private void validateAuthorizationType(Claims claims) {
        String authorizationType = claims.get(AUTHORIZATION_TYPE_CLAIM, String.class);

        if (!EMPLOYEE_AUTHORIZATION_TYPE.equals(authorizationType)) {
            throw new AuthenticationException(
                    "INVALID_TOKEN",
                    "JWT authorization type is invalid"
            );
        }
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException(
                    "INVALID_TOKEN",
                    "JWT is invalid"
            );
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (token.isBlank()) {
            throw new AuthenticationException(
                    "INVALID_TOKEN",
                    "JWT is invalid"
            );
        }

        return token;
    }
}