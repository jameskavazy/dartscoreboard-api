package com.jameskavazy.dartscoreboard.match.domain.model.entity;

import java.time.OffsetDateTime;

public record Set(
        String setId,
        String matchId,
        String setWinnerId,
        OffsetDateTime createdAt
) {
}
