package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.domain.*;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.exception.InvalidHierarchyException;
import com.jameskavazy.dartscoreboard.match.exception.InvalidPlayerTurnException;
import com.jameskavazy.dartscoreboard.match.exception.MatchNotFoundException;
import com.jameskavazy.dartscoreboard.match.models.legs.Leg;
import com.jameskavazy.dartscoreboard.match.models.matches.Match;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.models.sets.Set;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.models.visits.Visit;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final VisitRepository visitRepository;
    private final SetRepository setRepository;
    private final LegRepository legRepository;
    private final UserRepository userRepository;

    private final ScoreCalculator scoreCalculator;

    private final ProgressionHandler progressionHandler;

    public MatchService(MatchRepository matchRepository,
                        VisitRepository visitRepository,
                        SetRepository setRepository, LegRepository legRepository, UserRepository userRepository,
                        ScoreCalculator scoreCalculator,
                        ProgressionHandler progressionHandler){
        this.matchRepository = matchRepository;
        this.visitRepository = visitRepository;
        this.setRepository = setRepository;
        this.legRepository = legRepository;
        this.userRepository = userRepository;
        this.scoreCalculator = scoreCalculator;
        this.progressionHandler = progressionHandler;
    }


    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }


    public Optional<Match> findMatchById(String matchId) {
        return matchRepository.findById(matchId);
    }

    public void createMatch(MatchRequest matchRequest) {
        Match match = new Match(
                UUID.randomUUID().toString(),
                matchRequest.matchType(),
                matchRequest.raceToLeg(),
                matchRequest.raceToSet(),
                OffsetDateTime.now(),
                null,
                MatchStatus.ONGOING
        );
        matchRepository.create(match);

        // TODO create associated data - legs, sets, match users
    }

    public void updateMatch(Match match, String matchId) {
        matchRepository.update(match, matchId);
    }


    public VisitResult processVisitRequest(VisitRequest visitRequest,
                                           String matchId,
                                           String setId,
                                           String legId,
                                           String userPrincipalUsername) {

        String userId = validateUser(userPrincipalUsername);
        Match match = validateMatchHierarchy(matchId, legId, setId);
        validateTurn(matchId, legId, userId);

        int scoreRequest = visitRequest.score();
        int currentScore = visitRepository.extractCurrentScore(userId, legId);
        
        Visit visit = scoreCalculator.validateAndBuildVisit(userId, currentScore, scoreRequest, legId);
        visitRepository.create(visit);

        MatchContext matchContext = createMatchContext(matchId, setId, legId, userId, match, currentScore, visit);
        ResultScenario resultScenario = progressionHandler.checkResult(matchContext);
        ResultContext resultContext = handleResult(resultScenario, matchContext);
        // TODO: 28/06/2025 leg repository .update winner id... create new leg..

        return new VisitResult(resultScenario, resultContext);
    }

    private void validateTurn(String matchId, String legId, String userId) {
        int turnIndex = legRepository.getTurnIndex(legId);
        List<MatchesUsers> matchUsers = matchRepository.getMatchUsers(matchId);
        if (matchUsers == null || matchUsers.isEmpty()) {
            throw new MatchNotFoundException("No users associated with match " + matchId);
        }
        if (matchUsers.get(turnIndex).position() != turnIndex || !matchUsers.get(turnIndex).userId().equals(userId)){
            throw new InvalidPlayerTurnException("Player requested visit when it was not their turn");
        }
    }

    private Match validateMatchHierarchy(String matchId, String legId, String setId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException("Could not find match with id: " + matchId));

        if (!matchRepository.isValidLegHierarchy(legId, setId, matchId)){
            throw new InvalidHierarchyException(legId + " does not belong to specified set or match");
        }
        return match;
    }

    private String validateUser(String userPrincipalUsername){
        Optional<User> userOptional = userRepository.findByUsername(userPrincipalUsername);

        if (userOptional.isPresent()){
            User user = userOptional.get();
            return user.userId();
        } else {
            throw new UsernameNotFoundException("Could not insert visit: Could not find authorized user: " + userPrincipalUsername);
        }
    }

    private MatchContext createMatchContext(String matchId, String setId, String legId, String userId, Match match, int currentScore, Visit validatedVisit) {
        List<String> usersInMatch = matchRepository.getUsersIdsInMatch(matchId);
        int startingScore = matchRepository.getStartingScore(matchId);
        int legsWon = legRepository.countLegsWonInSet(userId, setId);
        int setsWon = setRepository.countSetsWonInMatch(userId, matchId);
        int finalScore = startingScore - currentScore - validatedVisit.score();

        return new MatchContext(
                match, usersInMatch, legsWon, setsWon, finalScore, legId, userId, setId
        );
    }

    private ResultContext handleResult(ResultScenario resultScenario, MatchContext matchContext) {
        return switch (resultScenario) {
            case NO_RESULT -> handleNoResult(matchContext);
            case LEG_WON -> handleLegWon(matchContext);
            case MATCH_WON -> handleMatchWon(matchContext);
            case SET_WON -> handleSetWon(matchContext);
        };
    }

    private ResultContext handleLegWon(MatchContext matchContext) {
        legRepository.updateWinnerId(matchContext.userId(), matchContext.legId());
        int numOfLegs = legRepository.countLegsInSet(matchContext.setId());
        int shift = setRepository.getSetsInMatch(matchContext.match().matchId()).size() - 1;

        // base on leg count, shift by which set we're in, less 1
        int turnIndex = nextPlayerIndex(matchContext, numOfLegs, shift);
        Leg leg = new Leg(
                UUID.randomUUID().toString(), matchContext.match().matchId(), matchContext.setId(), turnIndex, null, OffsetDateTime.now()
        );
        legRepository.create(leg);
        return new ResultContext(leg.legId(), matchContext.setId());
    }

    private ResultContext handleSetWon(MatchContext matchContext){

        legRepository.updateWinnerId(matchContext.userId(), matchContext.legId());
        setRepository.updateWinnerId(matchContext.userId(), matchContext.setId());

        String matchId = matchContext.match().matchId();
        int numOfSets = setRepository.getSetsInMatch(matchId).size();

        // base by numOfSets, no shift needed
        int turnIndex = nextPlayerIndex(matchContext, numOfSets, 0);

        Set set = new Set(UUID.randomUUID().toString(), matchId, null, OffsetDateTime.now());

        setRepository.create(set);

        Leg leg = new Leg(UUID.randomUUID().toString(), matchId, set.setId(), turnIndex, null, OffsetDateTime.now());
        legRepository.create(leg);

        return new ResultContext(leg.legId(), set.setId());
    }

    private ResultContext handleMatchWon(MatchContext matchContext){
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

    private ResultContext handleNoResult(MatchContext matchContext) {
        int currentTurnIndex = legRepository.getTurnIndex(matchContext.legId());
        int nextPlayerIndex = nextPlayerIndex(matchContext, currentTurnIndex, 1);
        legRepository.updateTurnIndex(nextPlayerIndex, matchContext.legId());

        return new ResultContext(matchContext.legId(), matchContext.setId());
    }

    private int nextPlayerIndex(MatchContext matchContext, int base, int shift) {
       return progressionHandler.increment(base, shift + matchContext.usersIdsInMatch().size(), matchContext.usersIdsInMatch().size());
    }
}

