package com.jameskavazy.dartscoreboard.match.domain.event;

import org.springframework.context.ApplicationEvent;

public class VisitSubmitEvent extends ApplicationEvent {


    private final String matchId;
    private final String setId;
    private final String legId;
    private final String visitId;
    private final String userId;


    public VisitSubmitEvent(Object source, String matchId, String setId, String legId, String visitId, String userId) {
        super(source);
        this.matchId = matchId;
        this.setId = setId;
        this.legId = legId;
        this.visitId = visitId;
        this.userId = userId;
    }
    public String getMatchId() {
        return matchId;
    }

    public String getSetId() {
        return setId;
    }

    public String getLegId() {
        return legId;
    }

    public String getVisitId() {
        return visitId;
    }

    public String getUserId() {
        return userId;
    }
}
