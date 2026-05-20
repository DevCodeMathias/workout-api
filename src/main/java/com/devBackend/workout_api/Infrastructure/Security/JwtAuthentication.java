package com.devBackend.workout_api.Infrastructure.Security;

import com.devBackend.workout_api.Application.Interface.IJwtAuthenticator;
import com.devBackend.workout_api.Domain.Exception.AuthenticationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtAuthentication implements IJwtAuthenticator {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void authenticate(String authorizationHeader) {
        getPayload(authorizationHeader);
    }

    @Override
    public void authenticateEmployee(String authorizationHeader, String employeeId) {
        String tokenEmployeeId = getAuthenticatedEmployeeId(authorizationHeader);

        if (!employeeId.equals(tokenEmployeeId)) {
            throw new AuthenticationException(
                    "TOKEN_EMPLOYEE_MISMATCH",
                    "JWT employee does not match requested employee"
            );
        }
    }

    @Override
    public String getAuthenticatedEmployeeId(String authorizationHeader) {
        String payload = getPayload(authorizationHeader);
        String employeeId = findClaim(payload, "employeeId");

        if (employeeId == null) {
            throw new AuthenticationException(
                    "INVALID_TOKEN",
                    "JWT does not contain employeeId"
            );
        }

        return employeeId;
    }

    private String getPayload(String authorizationHeader) {
        try {
            String token = authorizationHeader.replace(BEARER_PREFIX, "");
            String payload = token.split("\\.")[1];

            byte[] decodedPayload = Base64.getUrlDecoder().decode(payload);

            return new String(decodedPayload, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new AuthenticationException(
                    "INVALID_TOKEN",
                    "JWT is invalid"
            );
        }
    }

    private String findClaim(String payload, String claimName) {
        Pattern pattern = Pattern.compile("\"" + claimName + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(payload);

        return matcher.find() ? matcher.group(1) : null;
    }
}