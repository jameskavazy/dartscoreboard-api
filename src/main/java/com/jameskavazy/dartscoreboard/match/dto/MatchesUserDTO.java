package com.jameskavazy.dartscoreboard.match.dto;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;

public record MatchesUserDTO(
        String screenName,
        int position,
        InviteStatus inviteStatus) {

}
