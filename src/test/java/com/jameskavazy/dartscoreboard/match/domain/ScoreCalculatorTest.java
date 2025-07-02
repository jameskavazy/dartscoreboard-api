package com.jameskavazy.dartscoreboard.match.domain;

import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.exception.InvalidVisitScoreException;
import com.jameskavazy.dartscoreboard.match.model.visits.Visit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScoreCalculatorTest {

    ScoreCalculator scoreCalculator = new ScoreCalculator();

    @Test
    void shouldValidateAndBuildVisit(){

        Visit visit = scoreCalculator.validateAndBuildVisit(
                "user-1",
                60,
                new VisitRequest(58),
                "leg-id"
        );

        assertEquals(58, visit.score());
        assertEquals("user-1", visit.userId());
    }

    @Test
    void shouldThrowInvalidVisitScoreException(){
        assertThrows(InvalidVisitScoreException.class,
                () -> scoreCalculator.validateAndBuildVisit(
                        "user-1",
                        60,
                        new VisitRequest(199),
                        "legId"
                )) ;

    }
}