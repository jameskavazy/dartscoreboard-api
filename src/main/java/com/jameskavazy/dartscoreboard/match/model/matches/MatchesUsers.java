package com.jameskavazy.dartscoreboard.match.model.matches;

public record MatchesUsers(
        String matchId,
        String userId,
        int position,
        InviteStatus inviteStatus
) {
}
