package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.domain.aggregate.MatchContext;
import com.jameskavazy.dartscoreboard.match.domain.service.ProgressionHandler;
import com.jameskavazy.dartscoreboard.match.domain.model.value.ResultContext;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Leg;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchType;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Set;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameEngineTest {
    LegRepository legRepository = mock(LegRepository.class);
    SetRepository setRepository = mock(SetRepository.class);
    MatchRepository matchRepository = mock(MatchRepository.class);
    ProgressionHandler progressionHandler = mock(ProgressionHandler.class);
    GameEngine gameEngine = new GameEngine(legRepository, setRepository, matchRepository, progressionHandler);
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

        ResultContext resultContext = gameEngine.handleLegWon(matchContext);
        verify(legRepository).updateWinnerId("user-1", "leg-1");

        ArgumentCaptor<Leg> captor = ArgumentCaptor.forClass(Leg.class);
        verify(legRepository).create(captor.capture());
        Leg actual = captor.getValue();
        assertEquals("test-match-id",actual.matchId());

        assertEquals("set-1" ,resultContext.setId());
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

        ResultContext resultContext = gameEngine.handleSetWon(matchContext);
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
        when(progressionHandler.increment(anyInt(), anyInt(), anyInt())).thenReturn(2);
        ResultContext resultContext = gameEngine.handleNoResult(matchContext);
        verify(legRepository).updateTurnIndex(2, "leg-1");
        assertEquals("set-1", resultContext.setId());
        assertEquals("leg-1", resultContext.legId());
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
        verify(progressionHandler).checkResult(matchContext);
    }

}