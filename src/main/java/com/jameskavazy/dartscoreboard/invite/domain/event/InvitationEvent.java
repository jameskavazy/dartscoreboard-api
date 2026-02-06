package com.jameskavazy.dartscoreboard.invite.domain.event;

import com.jameskavazy.dartscoreboard.sse.dto.InvitationData;
import org.springframework.context.ApplicationEvent;

public class InvitationEvent extends ApplicationEvent {

    private final String userId;
    private final InvitationData invitationData;

    public InvitationEvent(Object source, String userId, InvitationData invitationData) {
        super(source);
        this.userId = userId;
        this.invitationData = invitationData;
    }

    public String getUserId() {
        return userId;
    }

    public InvitationData getInvitationData() {
        return invitationData;
    }
}
