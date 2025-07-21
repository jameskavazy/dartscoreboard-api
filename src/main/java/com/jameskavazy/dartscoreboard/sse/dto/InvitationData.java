package com.jameskavazy.dartscoreboard.sse.dto;

import com.jameskavazy.dartscoreboard.match.dto.MatchesUserDTO;
import com.jameskavazy.dartscoreboard.match.model.matches.Match;

import java.util.List;

public record InvitationData(
        Match match,
        List<MatchesUserDTO> users
) {
}
