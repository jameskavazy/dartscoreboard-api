package com.jameskavazy.dartscoreboard.match.domain.model.value;

public record PlayerState(
        String userId,
        int totalScore,
        boolean turn,
        int startingScore
) {
}
