package com.jameskavazy.dartscoreboard.match.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.auth.security.JwtFilter;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.SpringSecurityUserDetailsTestConfig;
import com.jameskavazy.dartscoreboard.match.models.matches.Match;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchType;
import com.jameskavazy.dartscoreboard.match.service.MatchService;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
@Import(SpringSecurityUserDetailsTestConfig.class)
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
    UserPrincipal userPrincipal;

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
                2
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
    @WithUserDetails
    void shouldReturnOKForCreateVisit() throws Exception {
        VisitRequest visitRequest = new VisitRequest(40);
        mvc.perform(post("/api/matches/match-1/sets/set-1/legs/leg-1/visits/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visitRequest))
                )
                .andExpect(status().isCreated());
    }
}