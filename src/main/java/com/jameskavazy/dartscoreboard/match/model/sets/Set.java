package com.jameskavazy.dartscoreboard.match.model.sets;

import java.time.OffsetDateTime;

public record Set(
        String setId,
        String matchId,
        String setWinnerId,
        OffsetDateTime createdAt
) {
}
