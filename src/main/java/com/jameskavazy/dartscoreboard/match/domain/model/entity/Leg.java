package com.jameskavazy.dartscoreboard.match.domain.model.entity;

import java.time.OffsetDateTime;

public record Leg(
        String legId,
        String matchId,

        String setId,
        int turnIndex,
        String winnerId,
        OffsetDateTime createdAt
) {
}
