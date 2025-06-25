package com.jameskavazy.dartscoreboard.auth.dto;

public record AuthResult(
        String username,
        String jwt

) {
}
