package com.jameskavazy.dartscoreboard.sse.controller;

import com.jameskavazy.dartscoreboard.GlobalExceptionHandler;
import com.jameskavazy.dartscoreboard.auth.security.JwtFilter;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.match.SpringSecurityUserDetailsTestConfig;
import com.jameskavazy.dartscoreboard.sse.controller.InviteSseController;
import com.jameskavazy.dartscoreboard.sse.impl.InviteEventEmitter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(InviteSseController.class)
@EnableAsync
@Import({SpringSecurityUserDetailsTestConfig.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class InviteSseControllerTest {

    @MockitoBean
    InviteEventEmitter inviteEventEmitter;
    @Autowired
    MockMvc mvc;

    @MockitoBean
    JwtFilter filter;

    @MockitoBean
    JwtService jwtService;
    @Test
    void shouldReceiveInviteEvent() throws Exception {
        String username = "user1@example.com";
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        SseEmitter emitter = new SseEmitter(1000L);
        when(inviteEventEmitter.subscribe(username, 1000L)).thenReturn(emitter);

        MvcResult result = mvc.perform(get("/api/sse/invite/" + username)
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(request().asyncStarted())
                .andReturn();

       Future<?> future = executorService.schedule(() -> {
            try {
                emitter.send(SseEmitter.event().name("invitation").data("match-514"));
                emitter.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, 200L, TimeUnit.MILLISECONDS);

       future.get();

        mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string("""
                        event:invitation
                        data:match-514

                        """));

        executorService.shutdown();
    }



}