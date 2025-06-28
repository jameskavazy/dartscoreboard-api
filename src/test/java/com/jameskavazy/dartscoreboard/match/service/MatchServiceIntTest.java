package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.auth.service.UserDetailsServiceImpl;
import com.jameskavazy.dartscoreboard.match.domain.ResultContext;
import com.jameskavazy.dartscoreboard.match.domain.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.domain.VisitResult;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.model.matches.Match;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchType;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

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

    final String matchId = "match-1";
    final String setId = "set-1";
    final String legId = "leg-1";


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

    @Test
    void processVisitRequest_returnMatchWon(){
        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        VisitResult visitResult = matchService
                .processVisitRequest(visitRequest,  matchId, setId, legId, "user3@example.com");

        VisitResult want = wantedVisitResultHelper(ResultScenario.MATCH_WON, legId, setId);
        assertEquals(want, visitResult);
    }

    @Test
    void processVisitRequest_returnSetWon(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 1, 2, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // Increase the set boundary for this test.

        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        VisitResult visitResult = matchService
                .processVisitRequest(visitRequest, matchId, setId, legId, "user3@example.com");


        assertEquals(ResultScenario.SET_WON, visitResult.resultScenario());
        assertNotEquals(legId, visitResult.resultContext().legId());
        assertNotEquals(setId, visitResult.resultContext().setId());
    }


    @Test
    void processVisitRequest_returnLegWon(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 2, 1, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // Increase the leg boundary for this test.

        legRepository.updateTurnIndex(2, "leg-1"); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        VisitResult visitResult = matchService
                .processVisitRequest(visitRequest, matchId, setId,legId, "user3@example.com");
        assertEquals(ResultScenario.LEG_WON, visitResult.resultScenario());
        assertNotEquals(legId, visitResult.resultContext().legId());
        assertEquals(setId, visitResult.resultContext().setId());
    }

    @Test
    void processVisitRequest_returnNoResult(){
        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(10);
        VisitResult visitResult = matchService
                .processVisitRequest(visitRequest, matchId, setId,legId, "user3@example.com");

        VisitResult want = wantedVisitResultHelper(ResultScenario.NO_RESULT, legId, setId);
        assertEquals(want, visitResult);
    }

    @Test
    void processVisitRequest_turnIndexCorrectlyLoopsOn_noResult(){
        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(10);
        VisitResult visitResult = matchService
                .processVisitRequest(visitRequest, matchId, setId, legId, "user3@example.com");

        int got = legRepository.getTurnIndex(visitResult.resultContext().legId());


        assertEquals(0, got);
    }
    @Test
    void processVisitRequest_turnIndexCorrectlyGoesUp_noResult(){
        legRepository.updateTurnIndex(1, legId); // Make sure it's user-2's turn
        VisitRequest visitRequest = new VisitRequest(10);
        VisitResult visitResult = matchService
                .processVisitRequest(visitRequest, matchId, setId,legId, "user2@example.com");

        int got = legRepository.getTurnIndex(visitResult.resultContext().legId());

        assertEquals(2, got);
    }

    @Test
    void processVisitRequest_turnIndexCorrect_setWon(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 1, 2, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // Increase the set boundary for this test.

        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        VisitResult visitResult =  matchService
                .processVisitRequest(visitRequest, matchId, setId, legId, "user3@example.com");

        int turnIndex = legRepository.getTurnIndex(visitResult.resultContext().legId());


        String userId = matchRepository.getMatchUsers(matchId).get(turnIndex).userId();


        assertEquals(1, turnIndex);
        assertEquals("user-2", userId);
    }
    @Test
    void processVisitRequest_turnIndexCorrectlyGoesUp_legWon(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 2, 1, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // Increase the leg boundary for this test.
        legRepository.updateTurnIndex(2, "leg-1"); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        VisitResult visitResult = matchService
                .processVisitRequest(visitRequest, matchId, setId,legId, "user3@example.com");

        int turnIndex = legRepository.getTurnIndex(visitResult.resultContext().legId());
        String userId = matchRepository.getMatchUsers(matchId).get(turnIndex).userId();

        assertEquals(1, turnIndex);
        assertEquals("user-2", userId);

    }

    @NotNull
    private VisitResult wantedVisitResultHelper(ResultScenario resultScenario, String legId, String setId) {
        ResultContext wantedContext = new ResultContext(legId, setId);
        return new VisitResult(resultScenario, wantedContext);
    }
}
