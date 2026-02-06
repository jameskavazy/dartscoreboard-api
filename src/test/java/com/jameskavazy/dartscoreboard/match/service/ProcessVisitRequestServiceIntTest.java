package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.auth.service.JwtService;
import com.jameskavazy.dartscoreboard.auth.service.UserDetailsServiceImpl;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Visit;
import com.jameskavazy.dartscoreboard.match.domain.service.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchType;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.user.UserRepository;
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
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@Testcontainers
@Transactional
@Rollback
public class ProcessVisitRequestServiceIntTest {

    @Autowired
    VisitProcessingService visitProcessingService;

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

//    @Test
//    void processVisitRequest_returnMatchWon(){
//        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
//        VisitRequest visitRequest = new VisitRequest(141);
//        VisitResult visitResult = visitProcessingService
//                .processVisitRequest(visitRequest,  matchId, setId, legId, "user3@example.com");
//
//        VisitResult want = wantedVisitResultHelper(ResultScenario.MATCH_WON, legId, setId);
//        assertEquals(want, visitResult);
//    }

    @Test
    void processVisitRequest_visitIsValidated(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 1, 2, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // Increase the set boundary for this test.

        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        visitProcessingService.processVisitRequest(visitRequest, matchId, setId, legId, "user3@example.com");
        List<Visit> visits = visitRepository.visitsInLeg(legId);
        visits.sort(Comparator.comparing(Visit::createdAt));

        assertEquals(141, visits.getLast().score());
        assertEquals("user-3", visits.getLast().userId());
    }


//    @Test
//    void processVisitRequest_returnLegWon(){
//        matchRepository.update(new Match(
//                "match-1", MatchType.FiveO, 2, 1, OffsetDateTime.now(), null, MatchStatus.ONGOING
//        ), "match-1"); // Increase the leg boundary for this test.
//
//        legRepository.updateTurnIndex(2, "leg-1"); // Make sure it's user-3's turn
//        VisitRequest visitRequest = new VisitRequest(141);
//        VisitResult visitResult = visitProcessingService
//                .processVisitRequest(visitRequest, matchId, setId,legId, "user3@example.com");
//        assertEquals(ResultScenario.LEG_WON, visitResult.resultScenario());
//        assertNotEquals(legId, visitResult.resultContext().legId());
//        assertEquals(setId, visitResult.resultContext().setId());
//    }
//
//    @Test
//    void processVisitRequest_returnNoResult(){
//        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
//        VisitRequest visitRequest = new VisitRequest(10);
//        VisitResult visitResult = visitProcessingService
//                .processVisitRequest(visitRequest, matchId, setId,legId, "user3@example.com");
//
//        VisitResult want = wantedVisitResultHelper(ResultScenario.NO_RESULT, legId, setId);
//        assertEquals(want, visitResult);
//    }

    @Test
    void processVisitRequest_turnIndexCorrectlyLoopsOn_noResult(){
        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(10);
        visitProcessingService
                .processVisitRequest(visitRequest, matchId, setId, legId, "user3@example.com");

        int got = legRepository.getTurnIndex(legId);


        assertEquals(0, got);
    }
    @Test
    void processVisitRequest_turnIndexCorrectlyGoesUp_noResult(){
        legRepository.updateTurnIndex(1, legId); // Make sure it's user-2's turn
        VisitRequest visitRequest = new VisitRequest(10);
        visitProcessingService
                .processVisitRequest(visitRequest, matchId, setId,legId, "user2@example.com");

        int got = legRepository.getTurnIndex(legId);

        assertEquals(2, got);
    }

    @Test
    void processVisitRequest_turnIndexCorrect_setWon(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 1, 2, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // Increase the set boundary for this test.

        legRepository.updateTurnIndex(2, legId); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);

        visitProcessingService
                .processVisitRequest(visitRequest, matchId, setId, legId, "user3@example.com");

        int wantedTurnIndex = legRepository.findActiveLegByMatchId(matchId).turnIndex();
        String wantedUserId = matchRepository.getMatchUsers(matchId).get(wantedTurnIndex).userId();

        assertEquals(1, wantedTurnIndex);
        assertEquals("user-2", wantedUserId);
    }
    @Test
    void processVisitRequest_turnIndexCorrectlyGoesUp_legWon(){
        matchRepository.update(new Match(
                "match-1", MatchType.FiveO, 2, 1, OffsetDateTime.now(), null, MatchStatus.ONGOING
        ), "match-1"); // Increase the leg boundary for this test.
        legRepository.updateTurnIndex(2, "leg-1"); // Make sure it's user-3's turn
        VisitRequest visitRequest = new VisitRequest(141);
        visitProcessingService
                .processVisitRequest(visitRequest, matchId, setId,legId, "user3@example.com");

        int turnIndex = legRepository.findActiveLegByMatchId(matchId).turnIndex();
        String userId = matchRepository.getMatchUsers(matchId).get(turnIndex).userId();

        assertEquals(1, turnIndex);
        assertEquals("user-2", userId);

    }

//    @NotNull
//    private VisitResult wantedVisitResultHelper(ResultScenario resultScenario, String legId, String setId) {
//        ResultContext wantedContext = new ResultContext(legId, setId);
//        return new VisitResult(resultScenario, wantedContext);
//    }
}
