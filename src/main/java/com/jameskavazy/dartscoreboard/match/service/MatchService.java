package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.exception.InvalidHierarchyException;
import com.jameskavazy.dartscoreboard.match.models.matches.Match;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.models.matches.MatchStatus;
import com.jameskavazy.dartscoreboard.match.models.visits.Visit;
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
    private final UserRepository userRepository;

    private final LegRepository legRepository;

    //TODO score calculator, turn-manager, progression handler classes

    public MatchService(MatchRepository matchRepository,
                        VisitRepository visitRepository,
                        UserRepository userRepository,
                        LegRepository legRepository){
        this.matchRepository = matchRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
        this.legRepository = legRepository;
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
    }

    public void updateMatch(Match match, String matchId) {
        matchRepository.update(match, matchId);
    }


    public void createVisit(VisitRequest visitRequest, String matchId, String setId, String legId, String userPrincipalUsername) {
        String userId = null;
        Optional<User> userOptional = userRepository.findByEmail(userPrincipalUsername);

        if (userOptional.isPresent()){
            User user = userOptional.get();
            userId = user.userId();
        } else {
            throw new UsernameNotFoundException("Could not insert visit: Could not find authorized user: " + userPrincipalUsername);
        }

        if(!legRepository.isValidLegHierarchy(legId, setId, matchId)){
            throw new InvalidHierarchyException(legId + " does not belong to specified set or match");
        }

        Visit visit = new Visit(
                UUID.randomUUID().toString(),
                legId,
                userId,
                visitRequest.score(),
                false, // TODO gameLOGIC
                OffsetDateTime.now()
        );

        visitRepository.create(visit);
    }
}
