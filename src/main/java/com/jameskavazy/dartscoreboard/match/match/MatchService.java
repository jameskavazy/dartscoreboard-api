package com.jameskavazy.dartscoreboard.match.match;

import com.jameskavazy.dartscoreboard.match.visit.Visit;
import com.jameskavazy.dartscoreboard.match.visit.VisitRepository;
import com.jameskavazy.dartscoreboard.match.visit.VisitRequest;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
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

    //TODO score calculator, turn-manager, progression handler classes

    public MatchService(MatchRepository matchRepository, VisitRepository visitRepository, UserRepository userRepository){
        this.matchRepository = matchRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }


    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }


    public Optional<Match> findMatchById(String matchId) {
        return matchRepository.findById(matchId);
    }

    public void createMatch(Match match) {
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

        // TODO repo: jdbc client to validate  legId belongs to set and matchIds...
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
