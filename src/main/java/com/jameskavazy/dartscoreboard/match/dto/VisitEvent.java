package com.jameskavazy.dartscoreboard.match.dto;

import com.jameskavazy.dartscoreboard.match.domain.model.value.PlayerState;
import com.jameskavazy.dartscoreboard.match.domain.model.value.VisitResult;

import java.util.List;

public record VisitEvent (
        List<PlayerState> playerStates,
        VisitResult visitResult
) {
}
