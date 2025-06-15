package com.jameskavazy.dartscoreboard.match.match;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;

public record Match(
        @NotEmpty
        String id,
        MatchType type,
        @PositiveOrZero
        int raceToLeg,
        @PositiveOrZero
        int raceToSet,
        @PastOrPresent
        OffsetDateTime createdAt,
        @PositiveOrZero
        long winnerId,
        Status status
    ){

}
