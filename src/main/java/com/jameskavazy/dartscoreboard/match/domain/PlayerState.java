package com.jameskavazy.dartscoreboard.match.domain;

public record PlayerState(
        String userId,
        int totalScore,
        boolean turn,
        int startingScore
) {
}
