package com.jameskavazy.dartscoreboard.match.domain.model.entity;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;

public record MatchesUsers(
        String matchId,
        String userId,
        int position,
        InviteStatus inviteStatus
) {
}
