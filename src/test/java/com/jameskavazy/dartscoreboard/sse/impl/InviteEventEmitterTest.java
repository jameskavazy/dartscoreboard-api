package com.jameskavazy.dartscoreboard.sse.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InviteEventEmitterTest {

    InviteEventEmitter inviteEventEmitter = new InviteEventEmitter();

    @Test
    void shouldSubscribeToInviteEmitter(){
        String username = "user1@example.com";
        SseEmitter emitter = inviteEventEmitter.subscribe(username, 1000L);

        assertTrue(inviteEventEmitter.getInviteEventEmitters().containsKey(username));
        assertEquals(inviteEventEmitter.getInviteEventEmitters().get(username), emitter);

    }

    @Test
    void shouldComplete_andRemoveKey(){
        String username = "user1@example.com";
        inviteEventEmitter.subscribe(username, 1000L);
        inviteEventEmitter.complete(username);

        assertFalse(inviteEventEmitter.getInviteEventEmitters().containsKey(username));
    }

}