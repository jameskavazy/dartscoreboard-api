package com.jameskavazy.dartscoreboard.sse.controller;

import com.jameskavazy.dartscoreboard.sse.impl.InviteEventEmitter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse/invite")
public class InviteSseController {

    private final InviteEventEmitter inviteEventEmitter;
    public InviteSseController(InviteEventEmitter inviteEventEmitter){
        this.inviteEventEmitter = inviteEventEmitter;
    }

    @GetMapping("/{userId}")
    public SseEmitter subscribeToInvites(@PathVariable String userId){
        return inviteEventEmitter.subscribe(userId, 1000L);
    }
}
