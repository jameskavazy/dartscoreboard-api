package com.jameskavazy.dartscoreboard.match.dto;

import com.jameskavazy.dartscoreboard.match.model.matches.MatchType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record MatchRequest(
        @NotNull
        MatchType matchType,
        @PositiveOrZero
        int raceToLeg,
        @PositiveOrZero
        int raceToSet,
        @NotNull
        List<String> screenNames

) {
}
