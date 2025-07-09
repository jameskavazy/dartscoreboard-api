package com.jameskavazy.dartscoreboard.match.controller;


import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.auth.service.UserDetailsServiceImpl;
import com.jameskavazy.dartscoreboard.match.domain.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.VisitResult;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.model.matches.Match;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchType;
import com.jameskavazy.dartscoreboard.sse.impl.MatchEventEmitter;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Testcontainers
@Transactional
@Rollback
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MatchControllerIntTest {

    @LocalServerPort
    int serverPort;

    RestClient restClient;

    @Autowired
    MatchRepository matchRepository;


    @Autowired
    MatchEventEmitter sseService;

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
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + serverPort)
                .defaultHeader("Authorization", "Bearer fakeToken")
                        .build();

        when(jwtService.getEmail("fakeToken")).thenReturn("user1@example.com");
        when(userDetailsService.loadUserByUsername("user1@example.com"))
                .thenReturn
                        (new UserPrincipal(
                                new User(
                                "user-1",
                                "user1@example.com",
                                "user3"))
                );
        doReturn(true)
                .when(jwtService)
                .validateToken(eq("fakeToken"), any(UserPrincipal.class));
    }

    @AfterEach
    void clearDb() {
        matchRepository.deleteAll();
    }
    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    @WithMockUser
    void shouldFindMatchById() {
        Match match = restClient.get().uri("/api/matches/match-1")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        assertEquals(
                "match-1", match.matchId()
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
                2,
                List.of("user1", "user2")
        );
        ResponseEntity<Void> newMatch = restClient.post().uri("/api/matches")
                .body(matchRequest)
                .retrieve()
                .toBodilessEntity();

        assertEquals(201, newMatch.getStatusCode().value());
    }

    @Test
    @WithMockUser
    void shouldNotCreateMatchWithInvalidRequest(){
        MatchRequest matchRequest = new MatchRequest(
                MatchType.FiveO,
                1,
                -1,
                List.of("user1", "user2")
        );

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.BadRequest.class ,() -> {
                    restClient.post().uri("/api/matches")
                            .body(matchRequest)
                            .retrieve()
                            .toBodilessEntity();
                });

        assertEquals(400, exception.getStatusCode().value());
        assertTrue(exception.getMessage().contains("raceToSet"));
    }

    @Test
    void shouldUpdateExistingMatch() {
        Match match = restClient.get()
                .uri("/api/matches/match-1")
                .retrieve()
                .body(Match.class);

        ResponseEntity<Void> updatedMatch = restClient.put()
                .uri("/api/matches/match-1")
                .body(match)
                .retrieve()
                .toBodilessEntity();

        assertEquals(204, updatedMatch.getStatusCode().value());
    }

    @Test
    void shouldCreateVisit_andReturnNoResult(){
        VisitRequest visitRequest = new VisitRequest(10);

        ResponseEntity<VisitResult> response = restClient.post()
                .uri("/api/matches/match-1/sets/set-1/legs/leg-1/visits/")
                .body(visitRequest)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<VisitResult>() {
                });


        assertEquals("leg-1", response.getBody().resultContext().legId());
        assertEquals("set-1", response.getBody().resultContext().setId());
        assertEquals(ResultScenario.NO_RESULT, response.getBody().resultScenario());
    }



}