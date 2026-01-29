package com.jameskavazy.dartscoreboard.sse.service;

import com.jameskavazy.dartscoreboard.invite.domain.event.InvitationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class InviteEventEmitter {

    private final Logger log = LoggerFactory.getLogger(InviteEventEmitter.class);
    private final ConcurrentHashMap<String, SseEmitter> inviteEventEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String key, long timeout) {
        SseEmitter emitter = new SseEmitter(timeout);
        inviteEventEmitters.putIfAbsent(key, emitter);
        emitter.onCompletion(() -> {
            emitter.complete();
            inviteEventEmitters.remove(key);
        });
        emitter.onTimeout(() -> {
            emitter.complete();
            inviteEventEmitters.remove(key);
        });

        return emitter;
    }

    @EventListener
    public void send(InvitationEvent invitationEvent) {
        String userId = invitationEvent.getUserId();
        try {
            if (inviteEventEmitters.get(userId) != null) {
                inviteEventEmitters.get(userId).send(SseEmitter
                        .event()
                        .name("invitation")
                        .data(invitationEvent.getInvitationData()));
            }
        } catch (IOException ex) {
            inviteEventEmitters.get(userId).complete();
            inviteEventEmitters.remove(userId);
             log.error("Cleaning up emitter - {}", ex.getMessage());
        }
    }

    public void complete(String key) {
        if (inviteEventEmitters.get(key) != null) {
            inviteEventEmitters.get(key).complete();
            inviteEventEmitters.remove(key);
        }
    }

    public ConcurrentHashMap<String, SseEmitter> getInviteEventEmitters() {
        return inviteEventEmitters;
    }
}
