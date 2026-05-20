package com.devBackend.workout_api.Infrastructure.Auth;

import com.devBackend.workout_api.Domain.Exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtAuthenticationService implements JwtAuthenticator {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationService.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void authenticate(String authorizationHeader) {
        logger.debug("Authenticating request JWT");
        parsePayload(extractToken(authorizationHeader));
        logger.debug("JWT authenticated");
    }

    @Override
    public void authenticateEmployee(String authorizationHeader, String employeeId) {
        logger.debug("Authenticating employee JWT for employee id: {}", employeeId);
        String tokenEmployeeId = getAuthenticatedEmployeeId(authorizationHeader);

        if (!employeeId.equals(tokenEmployeeId)) {
            logger.warn("JWT employee mismatch requestedEmployeeId={} tokenEmployeeId={}", employeeId, tokenEmployeeId);
            throw new AuthenticationException(
                    "TOKEN_EMPLOYEE_MISMATCH",
                    "JWT employee does not match requested employee"
            );
        }

        logger.debug("Employee JWT authenticated for employee id: {}", employeeId);
    }

    @Override
    public String getAuthenticatedEmployeeId(String authorizationHeader) {
        logger.debug("Extracting authenticated employee id from JWT");
        String payload = parsePayload(extractToken(authorizationHeader));
        String employeeId = extractEmployeeId(payload)
                .orElseThrow(() -> new AuthenticationException(
                        "INVALID_TOKEN",
                        "JWT does not contain an employee identifier"
                ));

        logger.debug("Authenticated employee id extracted from JWT");
        return employeeId;
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            logger.warn("JWT authentication failed: missing Authorization header");
            throw new AuthenticationException(
                    "AUTHORIZATION_HEADER_REQUIRED",
                    AUTHORIZATION_HEADER + " header is required"
            );
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("JWT authentication failed: Authorization header is not Bearer");
            throw new AuthenticationException(
                    "INVALID_AUTHORIZATION_HEADER",
                    AUTHORIZATION_HEADER + " header must use Bearer token"
            );
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            logger.warn("JWT authentication failed: empty token");
            throw new AuthenticationException("INVALID_TOKEN", "JWT is required");
        }

        return token;
    }

    private String parsePayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            logger.warn("JWT authentication failed: invalid token format");
            throw new AuthenticationException("INVALID_TOKEN", "JWT format is invalid");
        }

        try {
            byte[] decodedPayload = Base64.getUrlDecoder().decode(parts[1]);
            String payload = new String(decodedPayload, StandardCharsets.UTF_8).trim();
            if (!payload.startsWith("{") || !payload.endsWith("}")) {
                logger.warn("JWT authentication failed: invalid payload structure");
                throw new AuthenticationException("INVALID_TOKEN", "JWT payload is invalid");
            }

            return payload;
        } catch (Exception exception) {
            logger.warn("JWT authentication failed: payload could not be decoded");
            throw new AuthenticationException("INVALID_TOKEN", "JWT payload is invalid");
        }
    }

    private Optional<String> extractEmployeeId(String payload) {
        return extractStringClaim(payload, "employeeId")
                .or(() -> extractStringClaim(payload, "id"))
                .or(() -> extractStringClaim(payload, "sub"));
    }

    private Optional<String> extractStringClaim(String payload, String claim) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(claim) + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(payload);
        if (!matcher.find()) {
            return Optional.empty();
        }

        return Optional.of(matcher.group(1));
    }
}
