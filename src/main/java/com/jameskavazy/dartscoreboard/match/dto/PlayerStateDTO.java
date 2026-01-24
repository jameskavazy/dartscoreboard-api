package com.jameskavazy.dartscoreboard.match.dto;

public record PlayerStateDTO(
        String userId,
        int legsWon,
        int setsWon,
        int score,
        boolean isTurn,
        boolean finished
) {}
