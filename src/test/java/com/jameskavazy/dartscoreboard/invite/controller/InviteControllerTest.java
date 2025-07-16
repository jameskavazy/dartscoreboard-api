package com.jameskavazy.dartscoreboard.invite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.GlobalExceptionHandler;
import com.jameskavazy.dartscoreboard.auth.security.JwtFilter;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.match.SpringSecurityUserDetailsTestConfig;
import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InviteController.class)
@Import({SpringSecurityUserDetailsTestConfig.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class InviteControllerTest {

    @Autowired
    InviteController inviteController;

    @Autowired
    MockMvc mvc;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    JwtFilter jwtFilter;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldAcceptInvite_andReturn204() throws Exception {
        mvc.perform(put("/api/invites/match-4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(InviteStatus.ACCEPTED)))
                .andExpect(status().isNoContent());
    }

}