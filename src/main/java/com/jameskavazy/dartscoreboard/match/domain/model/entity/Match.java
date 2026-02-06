package com.jameskavazy.dartscoreboard.match.domain.model.entity;

import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchType;
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
