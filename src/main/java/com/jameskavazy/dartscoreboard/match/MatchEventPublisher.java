package com.jameskavazy.dartscoreboard.match;

import com.jameskavazy.dartscoreboard.match.domain.event.StateUpdateEvent;
import com.jameskavazy.dartscoreboard.match.domain.event.VisitSubmitEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class MatchEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;


    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishVisitSubmit(String matchId, String setId, String legId, String visitId, String userId) {
            publisher.publishEvent(new VisitSubmitEvent(this, matchId, setId, legId, visitId, userId));
    }

    public void publishStateUpdate(String matchId, String setId, String legId){
        publisher.publishEvent(new StateUpdateEvent(this, matchId, setId, legId));
    }
}
