package com.jameskavazy.dartscoreboard.match.match;

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
