package com.jameskavazy.dartscoreboard.sse.controller;

import com.jameskavazy.dartscoreboard.sse.impl.MatchEventEmitter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse/match")
public class MatchSseController {



    private final MatchEventEmitter matchEventEmitter;
    public MatchSseController(MatchEventEmitter matchEventEmitter){
        this.matchEventEmitter = matchEventEmitter;
    }

    @GetMapping("/{matchId}")
    public SseEmitter subscribeToMatchEmitter(@PathVariable String matchId){
        return matchEventEmitter.subscribe(matchId, 1000L);
    }
}
