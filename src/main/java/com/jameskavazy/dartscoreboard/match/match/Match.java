package com.jameskavazy.dartscoreboard.match.match;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;

public record Match(
        @NotEmpty
        String matchId,
        MatchType matchType,
        @PositiveOrZero
        int raceToLeg,
        @PositiveOrZero
        int raceToSet,
        @PastOrPresent
        OffsetDateTime createdAt,
        String winnerId,
        Status matchStatus
    ){

}
