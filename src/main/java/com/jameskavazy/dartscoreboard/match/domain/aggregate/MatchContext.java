package com.jameskavazy.dartscoreboard.match.domain.aggregate;

import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;

import java.util.List;

public record MatchContext(
        Match match,
        List<String> usersIdsInMatch,
        int legsWon,
        int setsWon,
        int computedScore,
        String legId,
        String userId,
        String setId

) {
}
