package com.jameskavazy.dartscoreboard.match.domain.event;

import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import org.springframework.context.ApplicationEvent;

public class VisitSubmitEvent extends ApplicationEvent {

    private final String matchId;
    private final String setId;
    private final String legId;
    private final VisitRequest visitRequest;

    public VisitSubmitEvent(Object source, String matchId, String setId, String legId, VisitRequest visitRequest) {
        super(source);

        this.matchId = matchId;
        this.setId = setId;
        this.legId = legId;
        this.visitRequest = visitRequest;
    }
}
