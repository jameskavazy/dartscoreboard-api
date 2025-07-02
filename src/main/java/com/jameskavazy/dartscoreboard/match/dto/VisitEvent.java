package com.jameskavazy.dartscoreboard.match.dto;

import com.jameskavazy.dartscoreboard.match.domain.PlayerState;
import com.jameskavazy.dartscoreboard.match.domain.VisitResult;

import java.util.List;

public record VisitEvent (
        List<PlayerState> playerStates,
        VisitResult visitResult
) {
}
