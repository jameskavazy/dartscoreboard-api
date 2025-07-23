package com.jameskavazy.dartscoreboard.match.service;


import com.jameskavazy.dartscoreboard.match.domain.MatchContext;
import com.jameskavazy.dartscoreboard.match.domain.ProgressionHandler;
import com.jameskavazy.dartscoreboard.match.domain.ResultContext;
import com.jameskavazy.dartscoreboard.match.domain.ResultScenario;
import com.jameskavazy.dartscoreboard.match.model.legs.Leg;
import com.jameskavazy.dartscoreboard.match.model.matches.Match;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.model.sets.Set;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class GameEngine {

    private final LegRepository legRepository;
    private final SetRepository setRepository;
    private final MatchRepository matchRepository;
    private final ProgressionHandler progressionHandler;

    public GameEngine(LegRepository legRepository, SetRepository setRepository, MatchRepository matchRepository, ProgressionHandler progressionHandler) {
        this.legRepository = legRepository;
        this.setRepository = setRepository;
        this.matchRepository = matchRepository;
        this.progressionHandler = progressionHandler;
    }

    public ResultContext handleLegWon(MatchContext matchContext) {
        legRepository.updateWinnerId(matchContext.userId(), matchContext.legId());
        int numOfLegs = legRepository.countLegsInSet(matchContext.setId());
        int setsInMatch = setRepository.getSetsInMatch(matchContext.match().matchId()).size() - 1;

        // Base on leg count, shift by which set we're in, less 1
        int turnIndex = nextPlayerIndex(matchContext, numOfLegs, setsInMatch);
        Leg newLeg = new Leg(
                UUID.randomUUID().toString(), matchContext.match().matchId(), matchContext.setId(), turnIndex, null, OffsetDateTime.now()
        );
        legRepository.create(newLeg);
        return new ResultContext(newLeg.legId(), matchContext.setId());
    }

    public ResultContext handleSetWon(MatchContext matchContext){

        legRepository.updateWinnerId(matchContext.userId(), matchContext.legId());
        setRepository.updateWinnerId(matchContext.userId(), matchContext.setId());

        String matchId = matchContext.match().matchId();
        int numOfSets = setRepository.getSetsInMatch(matchId).size();

        /*
         * Base by numOfSets
         * No offset needed as player who starts the set can be determined,
         * simply from the number of sets
         */
        int turnIndex = nextPlayerIndex(matchContext, numOfSets, 0);

        Set newSet = new Set(UUID.randomUUID().toString(), matchId, null, OffsetDateTime.now());

        setRepository.create(newSet);

        Leg newLeg = new Leg(UUID.randomUUID().toString(), matchId, newSet.setId(), turnIndex, null, OffsetDateTime.now());
        legRepository.create(newLeg);

        return new ResultContext(newLeg.legId(), newSet.setId());
    }

    public ResultContext handleMatchWon(MatchContext matchContext){
        legRepository.updateWinnerId(matchContext.userId(), matchContext.legId());
        setRepository.updateWinnerId(matchContext.userId(), matchContext.setId());
        Match match = new Match(
                matchContext.match().matchId(),
                matchContext.match().matchType(),
                matchContext.match().raceToLeg(),
                matchContext.match().raceToSet(),
                matchContext.match().createdAt(),
                matchContext.userId(),
                MatchStatus.COMPLETE
        );
        matchRepository.update(match, match.matchId());
        return new ResultContext(matchContext.legId(), matchContext.setId());
    }

    public ResultContext handleNoResult(MatchContext matchContext) {
        int currentTurnIndex = legRepository.getTurnIndex(matchContext.legId());
        int nextPlayerIndex = nextPlayerIndex(matchContext, currentTurnIndex, 1);
        legRepository.updateTurnIndex(nextPlayerIndex, matchContext.legId());
        return new ResultContext(matchContext.legId(), matchContext.setId());
    }

    public ResultScenario checkResult(MatchContext matchContext) {
        return progressionHandler.checkResult(matchContext);
    }

    private int nextPlayerIndex(MatchContext matchContext, int numberOfLegsOrSets, int offset) {
        return progressionHandler.increment(numberOfLegsOrSets, offset + matchContext.usersIdsInMatch().size(), matchContext.usersIdsInMatch().size());
    }

}
