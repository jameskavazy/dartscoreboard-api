package com.jameskavazy.dartscoreboard.match.models.sets;

import java.time.OffsetDateTime;

public record Set(
        String setId,
        String matchId,
        String setWinnerId,
        OffsetDateTime createdAt
) {
}
