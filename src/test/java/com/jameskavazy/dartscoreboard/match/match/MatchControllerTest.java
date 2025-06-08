package com.jameskavazy.dartscoreboard.match.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.Application;
import com.jameskavazy.dartscoreboard.config.SecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.when;
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

    @AfterEach
    void tearDown() {

    }

    @Test
    void findAll() throws Exception {
        when(repository.findAll()).thenReturn(matches);

        mvc.perform(MockMvcRequestBuilders.get("/api/matches/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", org.hamcrest.Matchers.is(matches.size()))
                );
    }

    @Test
    void findMatchById() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}