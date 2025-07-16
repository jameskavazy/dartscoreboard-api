package com.jameskavazy.dartscoreboard.match.model.matches;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;

public record MatchesUsers(
        String matchId,
        String userId,
        int position,
        InviteStatus inviteStatus
) {
}
