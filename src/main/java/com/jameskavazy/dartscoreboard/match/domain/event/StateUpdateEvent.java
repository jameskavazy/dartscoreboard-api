package com.jameskavazy.dartscoreboard.match.domain.event;


import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class StateUpdateEvent extends ApplicationEvent implements StateEvent {

    private final String matchId;
    private final List<PlayerStateDTO> playerStateDTOList;

    public StateUpdateEvent(Object source, String matchId, List<PlayerStateDTO> playerStateDTOList) {
        super(source);
        this.playerStateDTOList = playerStateDTOList;
        this.matchId = matchId;
    }

    public List<PlayerStateDTO> getPlayerStateDTOList() {
        return playerStateDTOList;
    }

    public String getMatchId() {
        return matchId;
    }
}
