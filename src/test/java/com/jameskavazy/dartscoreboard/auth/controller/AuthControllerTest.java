package com.jameskavazy.dartscoreboard.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.Application;
import com.jameskavazy.dartscoreboard.auth.dto.TokenRequest;
import com.jameskavazy.dartscoreboard.auth.dto.AuthResult;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.auth.security.JwtFilter;
import com.jameskavazy.dartscoreboard.auth.service.AuthService;
import com.jameskavazy.dartscoreboard.auth.config.SecurityConfig;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    JwtFilter filter;

    @MockitoBean
    JwtService jwtService;

    TokenRequest request = new TokenRequest("test");

    @Test
    void shouldAuthenticateAndReturnOk() throws Exception {
        when(authService.authenticate(request.token()))
                .thenReturn(Optional.of(new AuthResult("test.com", "jwt")));

        mockMvc.perform(post("/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + "jwt"))
                .andExpect(jsonPath("email").value("test.com"));
    }

    @Test
    void shouldNotAuthenticateAndReturn401TokenExpired() throws Exception {
        when(authService.authenticate(request.token())).thenReturn(Optional.empty());
        mockMvc.perform(post("/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("error").value("TokenExpired"))
                .andExpect(jsonPath("details").value("Your session has expired. Please sign in again."));
    }

    @Test
    void shouldNotAuthenticateAndReturn401TokenInvalid() throws Exception {
        when(authService.authenticate(request.token())).thenThrow(new InvalidTokenException("Token parsing failed: ", new IllegalArgumentException()));
        mockMvc.perform(post("/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("error").value("TokenInvalid"))
                .andExpect(jsonPath("details").value("Token parsing failed: "));
    }

}