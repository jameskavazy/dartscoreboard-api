package com.jameskavazy.dartscoreboard.sse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public void send(String key, Object data) {
        try {
            if (inviteEventEmitters.get(key) != null) {
                inviteEventEmitters.get(key).send(SseEmitter
                        .event()
                        .name("invitation")
                        .data(data));
            }
        } catch (IOException ex) {
            inviteEventEmitters.get(key).complete();
            inviteEventEmitters.remove(key);
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
