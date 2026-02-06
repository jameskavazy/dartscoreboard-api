package com.jameskavazy.dartscoreboard.match.domain.event;

import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;

import java.util.List;

public interface StateEvent {
    String getMatchId();
    List<PlayerStateDTO> getPlayerStateDTOList();
}
