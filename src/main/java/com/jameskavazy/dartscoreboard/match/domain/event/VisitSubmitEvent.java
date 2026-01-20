package com.jameskavazy.dartscoreboard.match.domain.event;

import com.jameskavazy.dartscoreboard.match.domain.model.entity.Visit;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class VisitSubmitEvent extends ApplicationEvent {

    private final String matchId;
    private final String setId;
    private final String legId;
    private final String visitId;


    public VisitSubmitEvent(Object source, String matchId, String setId, String legId, String visitId) {
        super(source);
        this.matchId = matchId;
        this.setId = setId;
        this.legId = legId;
        this.visitId = visitId;
    }
}
