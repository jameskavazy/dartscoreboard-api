package com.jameskavazy.dartscoreboard.sse.controller;

import com.jameskavazy.dartscoreboard.GlobalExceptionHandler;
import com.jameskavazy.dartscoreboard.auth.security.JwtFilter;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.match.SpringSecurityUserDetailsTestConfig;
import com.jameskavazy.dartscoreboard.sse.impl.MatchEventEmitter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
@WebMvcTest({MatchSseController.class, GlobalExceptionHandler.class})
@Import({SpringSecurityUserDetailsTestConfig.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class MatchSseControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    MatchEventEmitter matchEventEmitter;
    @MockitoBean
    JwtFilter filter;
    @MockitoBean
    JwtService jwtService;

    @Test
    void shouldReceiveMatchEvent() throws Exception {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        SseEmitter emitter = new SseEmitter(1000L);
        when(matchEventEmitter.subscribe("match-1", 1000L)).thenReturn(emitter);

        MvcResult result = mvc.perform(get("/api/sse/match/match-1")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(request().asyncStarted())
                .andReturn();

        executorService.schedule(() -> {
            try {
                emitter.send(SseEmitter.event().name("match-state").data("test data"));
                emitter.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, 200L, TimeUnit.MILLISECONDS);


        mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string("""
                        event:match-state
                        data:test data

                        """));
    }



    @Test
    void shouldReceiveEvent_multipleClients() throws Exception {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        SseEmitter emitter = new SseEmitter(1000L);
        SseEmitter emitter2 = new SseEmitter(1000L);
        when(matchEventEmitter.subscribe("match-1", 1000L))
                .thenReturn(emitter)
                .thenReturn(emitter2);


        MvcResult result = mvc.perform(get("/api/sse/match/match-1")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(request().asyncStarted())
                .andReturn();
        MvcResult result2 = mvc.perform(get("/api/sse/match/match-1")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(request().asyncStarted())
                .andReturn();


        executorService.schedule(() -> {
            try {
                emitter.send(SseEmitter.event().name("match-state").data("test data"));
                emitter.complete();
                emitter2.send(SseEmitter.event().name("match-state").data("test data 2"));
                emitter2.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, 200L, TimeUnit.MILLISECONDS);


        mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string("""
                        event:match-state
                        data:test data

                        """));

        mvc.perform(asyncDispatch(result2))
                .andExpect(status().isOk())
                .andExpect(content().string("""
                    event:match-state
                    data:test data 2

                    """));
    }
}