package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.auth.config.SecurityConfig;
import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.auth.service.UserDetailsServiceImpl;
import com.jameskavazy.dartscoreboard.match.SpringSecurityUserDetailsTestConfig;
import com.jameskavazy.dartscoreboard.match.domain.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.models.matches.Match;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchType;
import com.jameskavazy.dartscoreboard.match.models.visits.Visit;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest()
@Testcontainers
@Transactional
@Rollback
public class MatchServiceIntTest {
    @Autowired
    MatchService matchService;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    VisitRepository visitRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SetRepository setRepository;

    @Autowired
    LegRepository legRepository;

    @Autowired
    ScoreCalculator scoreCalculator;

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
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

//    @AfterEach
//    void clearDown(){
//        visitRepository.deleteLatestVisit("leg-1");
//    }

    @Test
    void processVisitRequest_returnMatchWon(){
        legRepository.updateTurnIndex(2, "leg-1"); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        ResultScenario resultScenario = matchService
                .processVisitRequest(visitRequest, "match-1", "set-1", "leg-1", "user3@example.com");
        assertEquals(ResultScenario.MATCH_WON, resultScenario);
    }

    @Test
    void processVisitRequest_returnSetWon(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 1, 2, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // INcrease the set boundary for this test.

        legRepository.updateTurnIndex(2, "leg-1"); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        ResultScenario resultScenario = matchService
                .processVisitRequest(visitRequest, "match-1", "set-1", "leg-1", "user3@example.com");
        assertEquals(ResultScenario.SET_WON, resultScenario);

    }

    @Test
    void processVisitRequest_returnLegWon(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 2, 1, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // INcrease the set boundary for this test.

        legRepository.updateTurnIndex(2, "leg-1"); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        ResultScenario resultScenario = matchService
                .processVisitRequest(visitRequest, "match-1", "set-1", "leg-1", "user3@example.com");
        assertEquals(ResultScenario.LEG_WON, resultScenario);
    }

//    @Test
//    void processVisitRequest_returnNoResult(){
//
//    }
//
//    @Test
//    void processVisitRequest_turnIndexCorrectlyLoopsOn_noResult(){
//
//    }
//    @Test
//    void processVisitRequest_turnIndexCorrectlyGoesUp_noResult(){
//
//    }
//
//    @Test
//    void processVisitRequest_turnIndexCorrect_setWon(){
//
//    }
//    @Test
//    void processVisitRequest_turnIndexCorrectlyGoesUp_legWon(){
//
//    }
}
