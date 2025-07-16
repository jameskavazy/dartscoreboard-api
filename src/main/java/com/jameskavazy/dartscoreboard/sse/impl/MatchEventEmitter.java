package com.jameskavazy.dartscoreboard.sse.impl;

import com.jameskavazy.dartscoreboard.sse.service.EventEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service
public class MatchEventEmitter implements EventEmitter {
    private final Logger log = LoggerFactory.getLogger(MatchEventEmitter.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final ConcurrentHashMap<String, List<SseEmitter>> matchEmitters = new ConcurrentHashMap<>();
    // possible List<Emitter + UserId object> to identify owner of emitter and custom logic per emitter?

    public SseEmitter subscribe(String matchId, long timeout) {
        SseEmitter emitter = new SseEmitter(timeout);
        matchEmitters.computeIfAbsent(matchId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> matchEmitters.get(matchId).remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            matchEmitters.get(matchId).remove(emitter);
        });
        return emitter;
    }

    public void send(String matchId, Object eventData) {
        List<SseEmitter> sseEmitters = matchEmitters.get(matchId);
        if (sseEmitters != null && !sseEmitters.isEmpty()) {
            sseEmitters.forEach(emitter -> executor.submit(() -> {
                try {
                    emitter.send(SseEmitter
                            .event()
                            .name("match_state")
                            .data(eventData));
                } catch (IOException e) {
                    matchEmitters.get(matchId).remove(emitter);
                    log.error("Cleaning up emitter - " + e.getMessage());
                }
            }));
        }
    }

    public void complete(String matchId) {
        List<SseEmitter> sseEmitters = matchEmitters.get(matchId);
        if (sseEmitters != null) {
            sseEmitters.forEach(ResponseBodyEmitter::complete);
            matchEmitters.remove(matchId);
        }
    }
    public ConcurrentHashMap<String, List<SseEmitter>> getMatchEmitters() {
        return matchEmitters;
    }
}
