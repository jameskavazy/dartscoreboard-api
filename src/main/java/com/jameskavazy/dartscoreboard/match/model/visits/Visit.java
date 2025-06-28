package com.jameskavazy.dartscoreboard.match.model.visits;

import java.time.OffsetDateTime;

public record Visit(
        String visitId,
        String legId,
        String userId,
        int score,
        boolean checkout,
        OffsetDateTime createdAt
) {
}
