package com.jameskavazy.dartscoreboard.match;

import com.jameskavazy.dartscoreboard.invite.domain.event.InvitationEvent;
import com.jameskavazy.dartscoreboard.match.domain.event.MatchStartEvent;
import com.jameskavazy.dartscoreboard.match.domain.event.StateUpdateEvent;
import com.jameskavazy.dartscoreboard.match.domain.event.VisitSubmitEvent;
import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
import com.jameskavazy.dartscoreboard.sse.dto.InvitationData;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;


    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishVisitSubmit(String matchId, String setId, String legId, String visitId, String userId) {
            publisher.publishEvent(new VisitSubmitEvent(this, matchId, setId, legId, visitId, userId));
    }

    public void publishStateUpdate(String matchId, List<PlayerStateDTO> playerStateDTOList){
        publisher.publishEvent(new StateUpdateEvent(this, matchId, playerStateDTOList));
    }

    public void publishInvite(String userId, InvitationData invitationData){
        publisher.publishEvent(new InvitationEvent(this, userId, invitationData));
    }

    public void publishMatchStart(String matchId, List<PlayerStateDTO> playerStateDTOList){
        publisher.publishEvent(new MatchStartEvent(this, matchId, playerStateDTOList));
    }
}
