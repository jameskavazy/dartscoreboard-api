package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.domain.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.exception.InvalidHierarchyException;
import com.jameskavazy.dartscoreboard.match.models.visits.Visit;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    VisitRepository visitRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MatchRepository matchRepository;

    @InjectMocks
    MatchService matchService;

    @Mock
    ScoreCalculator scoreCalculator;

    @Test
    void shouldCreateVisit() {
        VisitRequest visitRequest = new VisitRequest(180);

        String matchId = "match-1";
        String setId = "set-1";
        String legId = "leg-1";
        String userId = "user-1";

        User user = new User(userId, "user1@example.com", "user1@example.com");

        when(matchRepository.isValidLegHierarchy(legId, setId, matchId)).thenReturn(true);
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));
        when(visitRepository.extractCurrentScore(userId, legId)).thenReturn(301);
        when(scoreCalculator.validateAndBuildVisit(userId, 301, visitRequest.score(), legId))
                .thenReturn(new Visit(
                        "visit-4",
                        legId,
                        userId,
                        visitRequest.score(),
                        false,
                        OffsetDateTime.now()));


        matchService.createVisit(
                visitRequest, matchId, setId, legId, user.email()
        );

        verify(visitRepository)
                .create(argThat(visit ->
                        visit.legId().equals(legId)
                        && visit.userId().equals(userId)
                        && visit.score() == visitRequest.score()
                        && !visit.checkout()
                        && visit.createdAt() != null
                ));
    }

    @Test
    void shouldThrowUsernameNotFoundException(){
        VisitRequest visitRequest = new VisitRequest(180);

        String matchId = "match-1";
        String setId = "set-1";
        String legId = "leg-1";
        String userId = "user-1";

        User user = new User(userId, "user1@example.com", "user1@example.com");

        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> matchService.createVisit(
                visitRequest, matchId, setId, legId, user.email()
        ));
    }

    @Test
    void shouldThrowInvalidHierarchyException(){
        VisitRequest visitRequest = new VisitRequest(180);

        String matchId = "match-1";
        String setId = "set-2";
        String legId = "leg-1";
        String userId = "user-1";

        User user = new User(userId, "user1@example.com", "user1@example.com");

        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));
        when(matchRepository.isValidLegHierarchy(legId, setId, matchId)).thenReturn(false);

        assertThrows(InvalidHierarchyException.class, () -> matchService.createVisit(
                visitRequest, matchId, setId, legId, user.email()
        ));
    }
}