package com.jameskavazy.dartscoreboard.invite.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameskavazy.dartscoreboard.auth.security.JwtFilter;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.auth.service.UserDetailsServiceImpl;
import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchType;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.service.MatchService;
import com.jameskavazy.dartscoreboard.sse.service.InviteEventEmitter;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class InviteControllerIntTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    JwtFilter jwtFilter;
    @MockitoBean
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MatchRepository matchRepository;
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    MatchService matchService;
    @MockitoBean
    InviteEventEmitter inviteEventEmitter;
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );
    @BeforeAll
    static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }
    @BeforeEach
    void setUp() {
        when(jwtService.getEmail("fakeToken")).thenReturn("user1@example.com");
        when(userDetailsService.loadUserByUsername("user1@example.com"))
                .thenReturn
                        (new UserPrincipal(
                                new User(
                                        "user-1",
                                        "user1@example.com",
                                        "user1"))
                        );
        doReturn(true)
                .when(jwtService)
                .validateToken(eq("fakeToken"), any(UserPrincipal.class));
    }

    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldSendInvite_andReturnCreated() throws Exception {
        MatchRequest matchRequest = new MatchRequest(MatchType.FiveO, 1,1, List.of("user1", "user2", "user3"));

        mvc.perform(post("/api/invites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchRequest))
                        .header("Authorization", "Bearer fakeToken"))
                .andExpect(status().isCreated());

        List<Match> matches = matchRepository.findAll();
        Match match  = matches
                .stream()
                .filter(m -> m.createdAt().isAfter(OffsetDateTime.now().minusDays(1)))
                .findFirst()
                .orElseThrow();


        List<MatchesUsers> matchUsers = matchRepository.getMatchUsers(match.matchId());

        assertTrue(matchUsers.stream().allMatch(mu -> mu.inviteStatus().equals(InviteStatus.INVITED)));
        assertEquals(1, match.raceToLeg());
        assertEquals(1, match.raceToSet());
        assertEquals(MatchType.FiveO, match.matchType());
    }
}
