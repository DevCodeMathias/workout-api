package com.devBackend.workout_api.Infrastructure.Security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String secret;
    private long expirationHours;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setExpirationHours(long expirationHours) {
        this.expirationHours = expirationHours;
    }

    public long getExpirationHours() {
        return expirationHours;
    }
}
