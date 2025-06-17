package com.jameskavazy.dartscoreboard.match.dto;

import com.jameskavazy.dartscoreboard.match.models.matches.MatchType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record MatchRequest(
        @NotNull
        MatchType matchType,
        @PositiveOrZero
        int raceToLeg,
        @PositiveOrZero
        int raceToSet
) {
}
