package com.jameskavazy.dartscoreboard.match.controller;


import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.auth.service.UserDetailsServiceImpl;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.models.matches.Match;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchType;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MatchControllerIntTest {

    @LocalServerPort
    int serverPort;

    RestClient restClient;

    @Autowired
    MatchRepository matchRepository;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserDetailsServiceImpl userDetailsService;

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
        matchRepository.create(
                new Match("testMatchId", MatchType.FiveO, 1,1, OffsetDateTime.now(), null, MatchStatus.COMPLETE)
        );

        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + serverPort)
                .defaultHeader("Authorization", "Bearer fakeToken")
                        .build();

        when(jwtService.getEmail("fakeToken")).thenReturn("valid@username.com");
        when(userDetailsService.loadUserByUsername("valid@username.com")).thenReturn(new UserPrincipal(new User("valid","valid@username.com", "valid@username.com")));
        doReturn(true)
                .when(jwtService)
                .validateToken(eq("fakeToken"), any(UserPrincipal.class));
    }
    @AfterEach
    void cleanUp(){
        matchRepository.deleteAll();
    }

    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }
    @Test
    void shouldFindAllMatches() {
        List<Match> matches = restClient.get().uri("/api/matches")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        assertEquals(1, matches.size());
    }


    @Test
    @WithMockUser
    void shouldFindMatchById() {
        Match match = restClient.get().uri("/api/matches/testMatchId")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        assertEquals(
                "testMatchId", match.matchId()
        );
        assertEquals(
                MatchType.FiveO.name, match.matchType().name
        );
    }

    @Test
    @WithMockUser
    void shouldCreateMatch() {
        MatchRequest matchRequest = new MatchRequest(
                MatchType.FiveO,
                1,
                2
        );
        ResponseEntity<Void> newMatch = restClient.post().uri("/api/matches")
                .body(matchRequest)
                .retrieve()
                .toBodilessEntity();

        assertEquals(201, newMatch.getStatusCode().value());
    }

    @Test
    void shouldUpdateExistingMatch() {
        Match match = restClient.get()
                .uri("/api/matches/testMatchId")
                .retrieve()
                .body(Match.class);

        ResponseEntity<Void> updatedMatch = restClient.put()
                .uri("/api/matches/testMatchId")
                .body(match)
                .retrieve()
                .toBodilessEntity();

        assertEquals(204, updatedMatch.getStatusCode().value());
    }
}