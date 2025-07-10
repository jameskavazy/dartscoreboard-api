package com.jameskavazy.dartscoreboard.sse.impl;

import com.jameskavazy.dartscoreboard.match.dto.VisitEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @Test
    void shouldSendEvent() throws IOException {
        String username = "user1@example.com";
        SseEmitter emitter = mock(SseEmitter.class);


        inviteEventEmitter.getInviteEventEmitters().put(username, emitter);
        inviteEventEmitter.send(username, "testData");

        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }
}