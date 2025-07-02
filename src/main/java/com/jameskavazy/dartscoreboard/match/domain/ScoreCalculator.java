package com.jameskavazy.dartscoreboard.match.domain;

import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.exception.InvalidVisitScoreException;
import com.jameskavazy.dartscoreboard.match.model.visits.Visit;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class ScoreCalculator {


    public ScoreCalculator(){

    }

    private final Set<Integer> impossibleCheckouts = new HashSet<>(List.of(169, 168, 166, 165, 163, 162, 159));

    public Visit validateAndBuildVisit(String userId, int currentScore, VisitRequest visitRequest, String legId) {
        int scoreRequest = visitRequest.score();
        if (scoreRequest < 0 || scoreRequest > 180) {
            throw new InvalidVisitScoreException();
        }

        int validatedScore = validateScore(currentScore, scoreRequest);
        boolean checkout = isCheckout(currentScore);

        return new Visit(
                UUID.randomUUID().toString(),
                legId,
                userId,
                validatedScore,
                checkout,
                OffsetDateTime.now()
        );
    }

    private int validateScore(int playerScore, int input) {

        int newScore = playerScore - input;

        if (newScore < 0) {
            return 0;
        }

        if (newScore == 0) {
           if (isCheckout(playerScore)) {
               return input;
           }
           return 0;
        }
        if (newScore > 1) {
            return input;
        }
        return 0;
    }

    private boolean isCheckout(int currentScore){
         return currentScore < 171 && !impossibleCheckouts.contains(currentScore);
    }


}
