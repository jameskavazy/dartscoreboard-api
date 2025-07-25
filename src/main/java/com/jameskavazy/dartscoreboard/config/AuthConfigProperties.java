package com.jameskavazy.dartscoreboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth")
public record AuthConfigProperties(
        String googleClientId,
        String jwtSecret
) {
}
