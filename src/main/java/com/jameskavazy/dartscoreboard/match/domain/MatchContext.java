package com.jameskavazy.dartscoreboard.match.domain;

import com.jameskavazy.dartscoreboard.match.model.matches.Match;

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
