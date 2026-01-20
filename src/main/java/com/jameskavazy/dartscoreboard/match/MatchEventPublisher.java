package com.jameskavazy.dartscoreboard.match;

import com.jameskavazy.dartscoreboard.match.domain.event.VisitSubmitEvent;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Visit;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;


public class MatchEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;


    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void submitVisit(Visit visit) {
            publisher.publishEvent(new VisitSubmitEvent(this, visit));
    }


}
