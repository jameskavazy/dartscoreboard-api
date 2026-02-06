package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.EventPublisher;
import com.jameskavazy.dartscoreboard.match.domain.service.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.exception.InvalidHierarchyException;
import com.jameskavazy.dartscoreboard.match.exception.InvalidPlayerTurnException;
import com.jameskavazy.dartscoreboard.match.exception.MatchNotFoundException;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Visit;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
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
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public VisitProcessingService(MatchRepository matchRepository,
                                  LegRepository legRepository,
                                  VisitRepository visitRepository,
                                  ScoreCalculator scoreCalculator,
                                  UserRepository userRepository,
                                  EventPublisher eventPublisher) {
        this.matchRepository = matchRepository;
        this.legRepository = legRepository;
        this.visitRepository = visitRepository;
        this.scoreCalculator = scoreCalculator;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void processVisitRequest(VisitRequest visitRequest,
                                           String matchId,
                                           String setId,
                                           String legId,
                                           String userPrincipalUsername) {

        String userId = getValidatedUser(userPrincipalUsername);
        validateMatchHierarchy(matchId, legId, setId);
        validateTurn(matchId, legId, userId);

        int currentScore = visitRepository.extractCurrentScore(userId, legId);

        Visit visit = validateAndPersistVisit(visitRequest, legId, userId, currentScore);
        eventPublisher.publishVisitSubmit(matchId, setId, legId, visit.visitId(), userId);
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

    private void validateMatchHierarchy(String matchId, String legId, String setId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException("Could not find match with id: " + matchId));

        if (!matchRepository.isValidLegHierarchy(legId, setId, matchId)){
            throw new InvalidHierarchyException("Visit cannot be processed. " + legId + " does not belong to specified set or match");
        }
    }

    private String getValidatedUser(String userPrincipalUsername){
        Optional<User> userOptional = userRepository.findByUsername(userPrincipalUsername);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("Could not insert visit: Could not find authorized user: " + userPrincipalUsername));
        return user.userId();
    }
}
