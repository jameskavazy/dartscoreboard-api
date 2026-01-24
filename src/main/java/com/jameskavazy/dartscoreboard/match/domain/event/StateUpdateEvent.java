package com.jameskavazy.dartscoreboard.match.domain.event;


import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class StateUpdateEvent extends ApplicationEvent {

    private final List<PlayerStateDTO> playerStateDTOList;

    public StateUpdateEvent(Object source, List<PlayerStateDTO> playerStateDTOList) {
        super(source);
        this.playerStateDTOList = playerStateDTOList;
    }

    public List<PlayerStateDTO> getPlayerStateDTOList() {
        return playerStateDTOList;
    }
}
