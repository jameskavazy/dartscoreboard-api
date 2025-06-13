package com.jameskavazy.dartscoreboard.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth")
public record AuthConfigProperties(
        String googleClientId,
        String jwtSecret
) {
}
