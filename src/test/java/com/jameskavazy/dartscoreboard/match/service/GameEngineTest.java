package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.EventPublisher;
import com.jameskavazy.dartscoreboard.match.domain.aggregate.MatchContext;
import com.jameskavazy.dartscoreboard.match.domain.event.VisitSubmitEvent;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.*;
import com.jameskavazy.dartscoreboard.match.domain.model.value.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchType;
import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameEngineTest {
    LegRepository legRepository = mock(LegRepository.class);
    SetRepository setRepository = mock(SetRepository.class);
    MatchRepository matchRepository = mock(MatchRepository.class);
    VisitRepository visitRepository = mock(VisitRepository.class);
    EventPublisher eventPublisher = mock(EventPublisher.class);
    GameEngine gameEngine = new GameEngine(legRepository, setRepository, matchRepository, visitRepository, eventPublisher);

    @Test
    void shouldHandleLegWon() {
        Match match = new Match(
                "test-match-id",
                MatchType.FiveO,
                2,
                2,
                OffsetDateTime.now(),
                null,
                MatchStatus.ONGOING
        );

        MatchContext matchContext = new MatchContext(
                match,
                List.of("user-1", "user-2", "user-3"),
                0,
                0,
                0,
                "leg-1",
                "user-1",
                "set-1"
        );

        gameEngine.handleLegWon(matchContext);
        verify(legRepository).updateWinnerId("user-1", "leg-1");

        ArgumentCaptor<Leg> captor = ArgumentCaptor.forClass(Leg.class);
        verify(legRepository).create(captor.capture());
        Leg actual = captor.getValue();
        assertEquals("test-match-id",actual.matchId());

//        assertEquals("set-1" ,resultContext.setId());
    }

    @Test
    void shouldHandleSetWon() {
        Match match = new Match(
                "test-match-id",
                MatchType.FiveO,
                2,
                2,
                OffsetDateTime.now(),
                null,
                MatchStatus.ONGOING
        );

        MatchContext matchContext = new MatchContext(
                match,
                List.of("user-1", "user-2", "user-3"),
                0,
                0,
                0,
                "leg-1",
                "user-1",
                "set-1"
        );

        gameEngine.handleSetWon(matchContext);
        ArgumentCaptor<Leg> legArgumentCaptor = ArgumentCaptor.forClass(Leg.class);

        verify(legRepository).create(legArgumentCaptor.capture());
        Leg legActual = legArgumentCaptor.getValue();
        assertEquals("test-match-id",legActual.matchId());

        ArgumentCaptor<Set> setArgumentCaptor = ArgumentCaptor.forClass(Set.class);
        verify(setRepository).create(setArgumentCaptor.capture());
        assertEquals("test-match-id", setArgumentCaptor.getValue().matchId());
    }

    @Test
    void shouldHandleMatchWon(){
        Match match = new Match(
                "test-match-id",
                MatchType.FiveO,
                2,
                2,
                OffsetDateTime.now(),
                null,
                MatchStatus.ONGOING
        );

        MatchContext matchContext = new MatchContext(
                match,
                List.of("user-1", "user-2", "user-3"),
                0,
                0,
                0,
                "leg-1",
                "user-1",
                "set-1"
        );

        gameEngine.handleMatchWon(matchContext);

        ArgumentCaptor<Match> matchArgumentCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository).update(matchArgumentCaptor.capture(), anyString());
        Match actual = matchArgumentCaptor.getValue();
        assertEquals(MatchStatus.COMPLETE, actual.matchStatus());
        assertEquals("test-match-id", actual.matchId());
    }

    @Test
    void shouldHandleNoResult(){
        Match match = new Match(
                "test-match-id",
                MatchType.FiveO,
                2,
                2,
                OffsetDateTime.now(),
                null,
                MatchStatus.ONGOING
        );

        MatchContext matchContext = new MatchContext(
                match,
                List.of("user-1", "user-2", "user-3"),
                0,
                0,
                100,
                "leg-1",
                "user-1",
                "set-1"
        );
        when(legRepository.getTurnIndex(matchContext.legId())).thenReturn(1);
        gameEngine.handleNoResult(matchContext);
        verify(legRepository).updateTurnIndex(2, "leg-1");
    }

    @Test
    void shouldCallCheckResult(){
        Match match = new Match(
                "test-match-id",
                MatchType.FiveO,
                2,
                2,
                OffsetDateTime.now(),
                null,
                MatchStatus.ONGOING
        );

        MatchContext matchContext = new MatchContext(
                match,
                List.of("user-1", "user-2", "user-3"),
                0,
                0,
                100,
                "leg-1",
                "user-1",
                "set-1"
        );
        gameEngine.checkResult(matchContext);
//        verify(gameEngine).checkResult(matchContext);
    }

    Match match = new Match(
            "any-match", MatchType.FiveO, 3, 3, OffsetDateTime.now(), "", MatchStatus.ONGOING
    );

    List<String> userIds = List.of("user-1", "user-2");

    @Test
    void shouldReturnNoLegWon(){

        MatchContext matchContext = new MatchContext(
                match,  userIds, 1, 2, 100, "leg-1", "user-1", "set-1"
        );

        ResultScenario resultScenario = gameEngine.checkResult(matchContext);
        assertEquals(ResultScenario.NO_RESULT, resultScenario);
    }

    @Test
    void shouldReturnLegWonNoSetWon(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 1, 1, 0, "leg-1", "user-1", "set-1"
        );
        ResultScenario resultScenario = gameEngine.checkResult(matchContext);
        assertEquals(ResultScenario.LEG_WON, resultScenario);
    }

    @Test
    void shouldReturnLegWonSetWonNoMatchWon(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 2, 1, 0, "leg-1", "user-1", "set-1"
        );

        ResultScenario resultScenario = gameEngine.checkResult(matchContext);
        assertEquals(ResultScenario.SET_WON, resultScenario);
    }

    @Test
    void shouldReturnLegWonSetWonMatchWon(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 2, 2, 0, "leg-1", "user-1", "set-1"
        );

        ResultScenario resultScenario = gameEngine.checkResult(matchContext);
        assertEquals(ResultScenario.MATCH_WON, resultScenario);
    }

    @Test
    void shouldCorrectlyIncrementTurn(){

        MatchContext ctx = new MatchContext(
                match, userIds, 2, 2, 0, "leg-1", "user-1", "set-1");

        int next = gameEngine.nextPlayerIndex(ctx, 0, 1);
        assertEquals(1, next);
    }

    @Test
    void shouldCorrectlyDecrementTurn_cycleBackRound(){
        // given
        MatchContext ctx = new MatchContext(match, userIds, 2, 2, 0, "leg-1", "user-1", "set-1");

        // when
        int next = gameEngine.nextPlayerIndex(ctx, 0, -1);

        // then
        assertEquals(1, next);
    }

    @Test
    void shouldCorrectlyDecrementTurn() {
        // given
        MatchContext ctx = new MatchContext(match, List.of("user-1", "user-2", "user-3"), 2, 2, 0, "leg-1", "user-1", "set-1");

        // when
        int next = gameEngine.nextPlayerIndex(ctx, 3, -1);

        // then
        assertEquals(2, next);
    }

    @Test
    void handleVisitSubmitted_shouldPublishStateUpdate(){

        Match match = new Match("match-10", MatchType.FiveO, 1, 1, OffsetDateTime.now(), null, MatchStatus.ONGOING);
        VisitSubmitEvent event = new VisitSubmitEvent(
                "matchId", match.matchId(), "set-test", "leg-test", "visit-test", "user-2"
        );
        when(matchRepository.findById("match-10")).thenReturn(Optional.of(match));
//
        List<PlayerStateDTO> playerStateDTOS = List.of(new PlayerStateDTO("user-2", 0, 0, 0, true, true, 0, 0));
        when(matchRepository.getLatestStateForMatch(match.matchId())).thenReturn(playerStateDTOS);

        gameEngine.handleVisitSubmitted(event);
        verify(eventPublisher).publishStateUpdate("match-10", playerStateDTOS);
    }

}