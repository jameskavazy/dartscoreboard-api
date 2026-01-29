package com.jameskavazy.dartscoreboard.match.domain.event;

import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
import org.springframework.context.ApplicationEvent;

import java.util.List;


public class MatchStartEvent extends ApplicationEvent implements StateEvent {

    private final String matchId;
    private final List<PlayerStateDTO> playerStateDTOList;


    public MatchStartEvent(Object source, String matchId, List<PlayerStateDTO> playerStateDTOList) {
        super(source);
        this.matchId = matchId;
        this.playerStateDTOList = playerStateDTOList;
    }

    public String getMatchId() {
        return matchId;
    }

    public List<PlayerStateDTO> getPlayerStateDTOList() {
        return playerStateDTOList;
    }
}
