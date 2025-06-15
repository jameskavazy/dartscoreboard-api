package com.jameskavazy.dartscoreboard.match.match;


import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.auth.service.UserDetailsServiceImpl;
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
                new Match("testMatchId", MatchType.FiveO, 1,1, OffsetDateTime.now(), 1, Status.COMPLETE)
        );

        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + serverPort)
                .defaultHeader("Authorization", "Bearer fakeToken")
                        .build();

        when(jwtService.getEmail("fakeToken")).thenReturn("valid@email.com");
        when(userDetailsService.loadUserByUsername("valid@email.com")).thenReturn(new UserPrincipal(new User("valid@email.com", "ValidMan")));
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
        List<Match> matches = restClient.get().uri("/api/matches/all")
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
                "testMatchId", match.id()
        );
        assertEquals(
                MatchType.FiveO.name, match.type().name
        );
    }

    @Test
    @WithMockUser
    void shouldCreateMatch() {
       Match match = new Match("2ndTestMatchId", MatchType.FiveO, 3,3, OffsetDateTime.now(), 1, Status.COMPLETE);
        ResponseEntity<Void> newMatch = restClient.post().uri("/api/matches")
                .body(match)
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

    @Test
    void shouldDeleteMatch() {


        ResponseEntity<Void> match = restClient.delete()
                .uri("/api/matches/testMatchId")
                .retrieve()
                .toBodilessEntity();

        assertEquals(204, match.getStatusCode().value());

    }
}