package com.devbackend.workoutapi.infrastructure.security;

import com.devbackend.workoutapi.application.interfaces.JwtAuthenticator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                mock(JwtAuthenticator.class),
                mock(HandlerExceptionResolver.class)
        );
    }

    @Test
    void shouldNotFilterHealthCheckRoute() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/activities/healthCheck");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldFilterProtectedRoute() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/activities/me");

        assertFalse(filter.shouldNotFilter(request));
    }
}
