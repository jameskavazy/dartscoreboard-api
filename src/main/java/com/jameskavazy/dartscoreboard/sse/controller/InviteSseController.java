package com.jameskavazy.dartscoreboard.sse.controller;

import com.jameskavazy.dartscoreboard.sse.impl.InviteEventEmitter;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping("")
    public SseEmitter subscribeToInvites(@AuthenticationPrincipal UserDetails userDetails) {
        //TODO - change principal to our own impl, reduce coupling with SpringSec?
        String username = userDetails.getUsername();
        return inviteEventEmitter.subscribe(username, 1000L);
    }
}
