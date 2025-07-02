package com.jameskavazy.dartscoreboard.match.controller;

import com.jameskavazy.dartscoreboard.match.dto.VisitEvent;
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
public class SseService {

    private final Logger log = LoggerFactory.getLogger(SseService.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    ConcurrentHashMap<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    // possible List<Emitter + UserId object> to identify owner of emitter and custom logic per emitter?

    public SseEmitter subscribe(String matchId) {
        SseEmitter emitter = new SseEmitter(30000L);
        emitters.computeIfAbsent(matchId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> emitters.get(matchId).remove(emitter));
        emitter.onTimeout(() -> emitters.get(matchId).remove(emitter));
        return emitter;
    }

    public void sendToMatch(String matchId, VisitEvent eventData) {
        List<SseEmitter> sseEmitters = emitters.get(matchId);
        if (sseEmitters != null && !sseEmitters.isEmpty()) {
            sseEmitters.forEach(emitter -> executor.submit(() -> {
                try {
                    emitter.send(SseEmitter
                            .event()
                            .name("match_state")
                            .data(eventData));
                } catch (IOException e) {
                    emitters.get(matchId).remove(emitter);
                    log.error("Cleaning up emitter - " + e.getMessage());
                }
            }));
        }
    }

    public void complete(String matchId) {
        List<SseEmitter> sseEmitters = emitters.get(matchId);
        if (sseEmitters != null) {
            sseEmitters.forEach(ResponseBodyEmitter::complete);
            emitters.remove(matchId);
        }
    }
}
