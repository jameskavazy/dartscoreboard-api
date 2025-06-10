package com.jameskavazy.dartscoreboard.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.Application;
import com.jameskavazy.dartscoreboard.auth.dto.AuthResponse;
import com.jameskavazy.dartscoreboard.auth.dto.TokenRequest;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.auth.service.GoogleAuthService;
import com.jameskavazy.dartscoreboard.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    GoogleAuthService googleAuthService;

    TokenRequest request = new TokenRequest("test");

    @Test
    void shouldAuthenticateAndReturnOk() throws Exception {
        when(googleAuthService.authenticate(request.token())).thenReturn(Optional.of(new AuthResponse("test.com")));
        mockMvc.perform(post("/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("test.com"));
    }

    @Test
    void shouldNotAuthenticateAndReturn401TokenExpired() throws Exception {
        when(googleAuthService.authenticate(request.token())).thenReturn(Optional.empty());
        mockMvc.perform(post("/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("error").value("TokenExpired"))
                .andExpect(jsonPath("details").value("Your session has expired. Please sign in again."));
    }

    @Test
    void shouldNotAuthenticateAndReturn401TokenInvalid() throws Exception {
        when(googleAuthService.authenticate(request.token())).thenThrow(new InvalidTokenException("Token parsing failed: ", new IllegalArgumentException()));
        mockMvc.perform(post("/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("error").value("TokenInvalid"))
                .andExpect(jsonPath("details").value("Token parsing failed: "));
    }

}