package com.jameskavazy.dartscoreboard.match.domain.event;

import com.jameskavazy.dartscoreboard.match.domain.model.entity.Visit;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class VisitSubmitEvent extends ApplicationEvent {

    private final Visit visit;

    public VisitSubmitEvent(Object source, Visit visit) {
        super(source);
        this.visit = visit;
    }
}
