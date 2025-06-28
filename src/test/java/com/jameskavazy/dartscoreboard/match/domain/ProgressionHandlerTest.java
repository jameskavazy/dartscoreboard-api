package com.jameskavazy.dartscoreboard.match.domain;

import com.jameskavazy.dartscoreboard.match.model.matches.Match;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgressionHandlerTest {

    ProgressionHandler progressionHandler = new ProgressionHandler();

    Match match = new Match(
            "any-match", MatchType.FiveO, 3, 3, OffsetDateTime.now(), "", MatchStatus.ONGOING
    );
    List<String> userIds = List.of("user-1", "user-2");

    @Test
    void shouldReturnNoLegWon(){

        MatchContext matchContext = new MatchContext(
                match,  userIds, 1, 2, 100, "leg-1", "user-1", "set-1"
        );

        ResultScenario resultScenario = progressionHandler.checkResult(matchContext);
        assertEquals(ResultScenario.NO_RESULT, resultScenario);
    }

    @Test
    void shouldReturnLegWonNoSetWon(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 1, 1, 0, "leg-1", "user-1", "set-1"
        );
        ResultScenario resultScenario = progressionHandler.checkResult(matchContext);
        assertEquals(ResultScenario.LEG_WON, resultScenario);
    }

    @Test
    void shouldReturnLegWonSetWonNoMatchWon(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 2, 1, 0, "leg-1", "user-1", "set-1"
        );

        ResultScenario resultScenario = progressionHandler.checkResult(matchContext);
        assertEquals(ResultScenario.SET_WON, resultScenario);
    }

    @Test
    void shouldReturnLegWonSetWonMatchWon(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 2, 2, 0, "leg-1", "user-1", "set-1"
        );

        ResultScenario resultScenario = progressionHandler.checkResult(matchContext);
        assertEquals(ResultScenario.MATCH_WON, resultScenario);
    }

    @Test
    void shouldCorrectlyIncrementTurn(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 2, 2, 0, "leg-1", "user-1", "set-1"
        );
        int next = progressionHandler.increment(0, 1, matchContext.usersIdsInMatch().size());

        assertEquals(1, next);
    }

    @Test
    void shouldCorrectlyDecrementTurn_cycleBackRound(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 2, 2, 0, "leg-1", "user-1", "set-1"
        );
        int next = progressionHandler.increment(0, -1 + userIds.size(), userIds.size());

        assertEquals(1, next);
    }


    @Test
    void shouldCorrectlyDecrementTurn(){
        MatchContext matchContext = new MatchContext(
                match,  userIds, 2, 2, 0, "leg-1", "user-1", "set-1"
        );
        int next = progressionHandler.increment(3, -1 + 3, 3);

        assertEquals(2, next);
    }

}