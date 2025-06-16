package com.jameskavazy.dartscoreboard.match.match;

import jakarta.validation.constraints.PastOrPresent;

import java.time.OffsetDateTime;

public record Match(
        String matchId,
        MatchType matchType,
        int raceToLeg,
        int raceToSet,
        @PastOrPresent
        OffsetDateTime createdAt,
        String winnerId,
        MatchStatus matchStatus
    ){

}
