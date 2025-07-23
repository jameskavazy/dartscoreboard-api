package com.jameskavazy.dartscoreboard.invite.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.GlobalExceptionHandler;
import com.jameskavazy.dartscoreboard.auth.security.JwtFilter;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.invite.service.InviteService;
import com.jameskavazy.dartscoreboard.match.SpringSecurityUserDetailsTestConfig;
import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchType;
import com.jameskavazy.dartscoreboard.match.service.MatchService;
import com.jameskavazy.dartscoreboard.match.service.MatchSetupService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InviteController.class)
@Import({SpringSecurityUserDetailsTestConfig.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class InviteControllerTest {

    @Autowired
    InviteController inviteController;

    @MockitoBean
    MatchService matchService;

    @MockitoBean
    InviteService inviteService;

    @MockitoBean
    MatchSetupService matchSetupService;

    @Autowired
    MockMvc mvc;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    JwtFilter jwtFilter;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithUserDetails
    void shouldReturn204() throws Exception {
        mvc.perform(put("/api/invites/match-4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(InviteStatus.ACCEPTED)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnIsCreated() throws Exception {
        MatchRequest matchRequest = new MatchRequest(MatchType.FiveO, 1,1, List.of("user1", "user2", "user3"));

        mvc.perform(post("/api/invites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matchRequest))
                )
                .andExpect(status().isCreated());
    }
}