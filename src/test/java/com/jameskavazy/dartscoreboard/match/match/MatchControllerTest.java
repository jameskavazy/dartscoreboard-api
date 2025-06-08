package com.jameskavazy.dartscoreboard.match.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.Application;
import com.jameskavazy.dartscoreboard.config.SecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
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
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
class MatchControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MatchRepository repository;

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
                        0));
    }


    @Test
    void shouldFindAllMatches() throws Exception {
        when(repository.findAll()).thenReturn(matches);

        mvc.perform(get("/api/matches/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(matches.size()))
                );
    }

    @Test
    void shouldReturnMatchFromValidId() throws Exception {
        Match match = matches.get(0);

        when(repository.findById("first")).thenReturn(Optional.of(match));

        mvc.perform(get("/api/matches/first"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(match.id())))
                .andExpect(jsonPath("$.type", is(match.type().name())))
                .andExpect(jsonPath("$.raceToLeg", is(match.raceToLeg())))
                .andExpect(jsonPath("$.raceToSet", is(match.raceToSet())))
                .andExpect(jsonPath("$.createdAt", is(match.createdAt().toString())))
                .andExpect(jsonPath("$.winnerId", is((int) match.winnerId())));

    }

    @Test
    void shouldReturnNotFoundWithInvalidId() throws Exception {
        mvc.perform(get("/api/matches/3000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateMatch() throws Exception {
        Match match = new Match(
                "2nd",
                MatchType.FiveO,
                1,
                2,
                OffsetDateTime.parse("2025-06-08T13:12:02.221101+01:00"),
                0);

        mvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(match))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateMatch() throws Exception {
        Match match = new Match(
                "2nd",
                MatchType.FiveO,
                1,
                2,
                OffsetDateTime.parse("2025-06-08T13:12:02.221101+01:00"),
                0);
        mvc.perform(put("/api/matches/first")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(match))
            )
                .andExpect(status().isNoContent());

    }

    @Test
    void shouldDelete() throws Exception {
        mvc.perform(delete("/api/matches/first"))
                .andExpect(status().isNoContent());

    }
}