package com.jameskavazy.dartscoreboard.match.controller;

import com.jameskavazy.dartscoreboard.match.domain.PlayerState;
import com.jameskavazy.dartscoreboard.match.domain.ResultContext;
import com.jameskavazy.dartscoreboard.match.domain.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.VisitResult;
import com.jameskavazy.dartscoreboard.match.dto.VisitEvent;

import com.jameskavazy.dartscoreboard.sse.impl.MatchEventEmitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SseServiceTest {

    MatchEventEmitter matchEventEmitter = new MatchEventEmitter();

    VisitResult visitResult = new VisitResult(ResultScenario.NO_RESULT, new ResultContext("leg-1", "set-1"));

    List<PlayerState> playerStates = List.of(
            new PlayerState("user-1", 10, true, 501),
            new PlayerState("user-2", 100, false, 501));

    @Test
    void shouldRemoveEmitterOnTimeout() throws InterruptedException {
        String matchId = "match-1";

        SseEmitter emitter = matchEventEmitter.subscribe(matchId, 1000L);
        assertTrue(matchEventEmitter.getMatchEmitters().containsKey(matchId));
        assertTrue(matchEventEmitter.getMatchEmitters().get(matchId).contains(emitter));

        Thread.sleep(5000);
        emitter.onTimeout(() -> {
            assertFalse(matchEventEmitter.getMatchEmitters().get(matchId).contains(emitter));
        });
    }

    @Test
    void shouldSendToMatch() throws Exception {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Field executorField = MatchEventEmitter.class.getDeclaredField("executor");
        executorField.setAccessible(true);
        executorField.set(matchEventEmitter, executorService);


        String matchId = "match-1";
        SseEmitter emitter = mock(SseEmitter.class);
        VisitEvent visitEvent = new VisitEvent(playerStates, visitResult);

        matchEventEmitter.getMatchEmitters().put(matchId, new CopyOnWriteArrayList<>(List.of(emitter)));
        matchEventEmitter.send(matchId, visitEvent);

        executorService.shutdown();
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }

}