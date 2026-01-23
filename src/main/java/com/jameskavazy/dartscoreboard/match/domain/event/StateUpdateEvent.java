package com.jameskavazy.dartscoreboard.match.domain.event;


import org.springframework.context.ApplicationEvent;

public class StateUpdateEvent extends ApplicationEvent {

    private final String matchId;
    private final String setId;
    private final String legId;

    public StateUpdateEvent(Object source, String matchId, String setId, String legId) {
        super(source);
        this.matchId = matchId;
        this.setId = setId;
        this.legId = legId;
    }

    public String getLegId() {
        return legId;
    }

    public String getSetId() {
        return setId;
    }

    public String getMatchId() {
        return matchId;
    }
}
