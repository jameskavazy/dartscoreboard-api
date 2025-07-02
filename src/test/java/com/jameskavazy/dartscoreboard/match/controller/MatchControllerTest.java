package com.jameskavazy.dartscoreboard.match.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.GlobalExceptionHandler;
import com.jameskavazy.dartscoreboard.auth.security.JwtFilter;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.match.domain.ResultContext;
import com.jameskavazy.dartscoreboard.match.domain.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.VisitResult;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.SpringSecurityUserDetailsTestConfig;
import com.jameskavazy.dartscoreboard.match.model.matches.Match;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchType;
import com.jameskavazy.dartscoreboard.match.service.MatchService;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({MatchController.class, GlobalExceptionHandler.class})
@Import({SpringSecurityUserDetailsTestConfig.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class MatchControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MatchService matchService;

    @MockitoBean
    JwtFilter filter;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    SseService sseService;

    @MockitoBean
    UserPrincipal userPrincipal;
    @Autowired(required = false)
    GlobalExceptionHandler globalExceptionHandler;
    private final List<Match> matches = new ArrayList<>();

    @BeforeEach
    void setUp() {
        matches.add(
                new Match(
                        "first",
                        MatchType.FiveO,
                        1,
                        2,
                        OffsetDateTime.parse("2025-06-08T13:12:02.221101+01:00"),
                        null,
                        MatchStatus.ONGOING));

        assertNotNull(globalExceptionHandler, "GlobalExceptionHandler not loaded");
    }





    @Test
    void shouldFindAllMatches() throws Exception {
        when(matchService.findAllMatches()).thenReturn(matches);

        mvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(matches.size()))
                );
    }

    @Test
    void shouldReturnMatchFromValidId() throws Exception {
        Match match = matches.get(0);

        when(matchService.findMatchById("first")).thenReturn(Optional.of(match));

        mvc.perform(get("/api/matches/first"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchId", is(match.matchId())))
                .andExpect(jsonPath("$.matchType", is(match.matchType().name())))
                .andExpect(jsonPath("$.raceToLeg", is(match.raceToLeg())))
                .andExpect(jsonPath("$.raceToSet", is(match.raceToSet())))
                .andExpect(jsonPath("$.createdAt", is(match.createdAt().toString())))
                .andExpect(jsonPath("$.winnerId", is(match.winnerId())));

    }

    @Test
    void shouldReturnNotFoundWithInvalidId() throws Exception {
        mvc.perform(get("/api/matches/threethousand"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateMatch() throws Exception {
        MatchRequest matchRequest = new MatchRequest(
                MatchType.FiveO,
                1,
                2,
                List.of("user-1", "user-2")
        );

        mvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchRequest))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateMatch() throws Exception {
        Match match = new Match(
                "match-1",
                MatchType.FiveO,
                1,
                2,
                OffsetDateTime.parse("2025-06-14T13:12:02.221101+01:00"),
                null,
                MatchStatus.ONGOING);
        mvc.perform(put("/api/matches/match-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(match))
            )
                .andExpect(status().isNoContent());

    }

    @Test
    @WithUserDetails()
    void shouldReturnCREATEDForCreateVisit() throws Exception {
        VisitRequest visitRequest = new VisitRequest(40);
        when(matchService.processVisitRequest(
               ArgumentMatchers.any(VisitRequest.class),
                eq("match-1"),
                eq("set-1"),
                eq("leg-1"),
                anyString()))
                .thenReturn(new VisitResult(ResultScenario.NO_RESULT, new ResultContext("leg-1", "set-1")));



        mvc.perform(post("/api/matches/match-1/sets/set-1/legs/leg-1/visits/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visitRequest))
                )
                .andExpect(jsonPath("$.resultScenario").value("NO_RESULT"))
                .andExpect(jsonPath("$.resultContext.legId").value("leg-1"))
                .andExpect(jsonPath("$.resultContext.setId").value("set-1"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReceiveEvent(){
//
//        mvc.perform(get("/api/matches/match-1/sse"))
//                .andExpect(e)
    }
}