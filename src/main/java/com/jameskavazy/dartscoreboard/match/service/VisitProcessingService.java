package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.MatchEventPublisher;
import com.jameskavazy.dartscoreboard.match.domain.aggregate.MatchContext;
import com.jameskavazy.dartscoreboard.match.domain.model.value.PlayerState;
import com.jameskavazy.dartscoreboard.match.domain.model.value.ResultContext;
import com.jameskavazy.dartscoreboard.match.domain.model.value.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.model.value.VisitResult;
import com.jameskavazy.dartscoreboard.match.domain.service.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.dto.VisitEvent;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.exception.InvalidHierarchyException;
import com.jameskavazy.dartscoreboard.match.exception.InvalidPlayerTurnException;
import com.jameskavazy.dartscoreboard.match.exception.MatchNotFoundException;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Visit;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.sse.impl.MatchEventEmitter;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VisitProcessingService {

    private final MatchRepository matchRepository;
    private final LegRepository legRepository;
    private final VisitRepository visitRepository;
    private final ScoreCalculator scoreCalculator;
    private final MatchEventEmitter matchEventEmitter;
    private final UserRepository userRepository;
    private final MatchEventPublisher matchEventPublisher;

    public VisitProcessingService(MatchRepository matchRepository,
                                  LegRepository legRepository,
                                  VisitRepository visitRepository,
                                  ScoreCalculator scoreCalculator,
                                  MatchEventEmitter matchEventEmitter,
                                  UserRepository userRepository,
                                  MatchEventPublisher matchEventPublisher) {
        this.matchRepository = matchRepository;
        this.legRepository = legRepository;
        this.visitRepository = visitRepository;
        this.scoreCalculator = scoreCalculator;
        this.matchEventEmitter = matchEventEmitter;
        this.userRepository = userRepository;
        this.matchEventPublisher = matchEventPublisher;
    }

    @Transactional
    public void processVisitRequest(VisitRequest visitRequest,
                                           String matchId,
                                           String setId,
                                           String legId,
                                           String userPrincipalUsername) {

        String userId = validateUser(userPrincipalUsername);
        validateMatchHierarchy(matchId, legId, setId);
        validateTurn(matchId, legId, userId);

        int currentScore = visitRepository.extractCurrentScore(userId, legId);

        Visit visit = validateAndPersistVisit(visitRequest, legId, userId, currentScore);
        matchEventPublisher.publishVisitSubmit(matchId, setId, legId, visit.visitId(), userId);
    }

    private void validateTurn(String matchId, String legId, String userId) {
        int turnIndex = legRepository.getTurnIndex(legId);
        List<MatchesUsers> matchUsers = matchRepository.getMatchUsers(matchId); // Always ordered by position
        if (matchUsers == null || matchUsers.isEmpty()) {
            throw new MatchNotFoundException("No users associated with match " + matchId);
        }
        if (matchUsers.get(turnIndex).position() != turnIndex || !matchUsers.get(turnIndex).userId().equals(userId)){
            throw new InvalidPlayerTurnException("Player requested visit when it was not their turn");
        }
    }

    private Visit validateAndPersistVisit(VisitRequest visitRequest, String legId, String userId, int currentScore) {
        Visit visit = scoreCalculator.validateAndBuildVisit(userId, currentScore, visitRequest, legId);
        visitRepository.create(visit);
        return visit;
    }

    private void notifyClients(String matchId, String legId, VisitResult visitResult) {
        List<PlayerState> playerStates = visitRepository.getMatchData(legId);
        matchEventEmitter.send(matchId, new VisitEvent(playerStates, visitResult));

        if (visitResult.resultScenario().equals(ResultScenario.MATCH_WON)) {
            matchEventEmitter.complete(matchId);
        }
    }

    private void validateMatchHierarchy(String matchId, String legId, String setId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException("Could not find match with id: " + matchId));

        if (!matchRepository.isValidLegHierarchy(legId, setId, matchId)){
            throw new InvalidHierarchyException("Visit cannot be processed. " + legId + " does not belong to specified set or match");
        }
    }

    private String validateUser(String userPrincipalUsername){
        Optional<User> userOptional = userRepository.findByUsername(userPrincipalUsername);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("Could not insert visit: Could not find authorized user: " + userPrincipalUsername));
        return user.userId();
    }
}
