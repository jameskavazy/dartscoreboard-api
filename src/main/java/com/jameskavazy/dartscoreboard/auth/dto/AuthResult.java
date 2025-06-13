package com.jameskavazy.dartscoreboard.auth.dto;

public record AuthResult(
        String email,
        String jwt

) {
}
