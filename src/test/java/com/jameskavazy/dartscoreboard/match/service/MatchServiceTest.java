package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.controller.SseService;
import com.jameskavazy.dartscoreboard.match.domain.ProgressionHandler;
import com.jameskavazy.dartscoreboard.match.domain.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.dto.VisitEvent;
import com.jameskavazy.dartscoreboard.match.exception.InvalidHierarchyException;
import com.jameskavazy.dartscoreboard.match.model.matches.*;
import com.jameskavazy.dartscoreboard.match.model.visits.Visit;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
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
import java.util.List;
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
    @Mock
    LegRepository legRepository;
    @Mock
    ScoreCalculator scoreCalculator;

    @Mock
    SetRepository setRepository;

    @Mock
    ProgressionHandler progressionHandler;

    @Mock
    SseService sseService;

    @InjectMocks
    MatchService matchService;

    @Test
    void processVisitRequest_shouldProcessWithValidData() {
        VisitRequest visitRequest = new VisitRequest(180);

        String matchId = "match-1";
        String setId = "set-1";
        String legId = "leg-1";
        String userId = "user-1";

        User user = new User(userId, "user1@example.com", "user1@example.com");

        when(matchRepository.isValidLegHierarchy(legId, setId, matchId)).thenReturn(true);
        when(userRepository.findByUsername("user1@example.com")).thenReturn(Optional.of(user));
        when(visitRepository.extractCurrentScore(userId, legId)).thenReturn(301);
        when(matchRepository.findById(matchId)).thenReturn(
                Optional.of(new Match(matchId, MatchType.FiveO, 3,3,
                        OffsetDateTime.now(), null, MatchStatus.ONGOING))
        );
        when(matchRepository.getMatchUsers(matchId)).thenReturn(List.of(new MatchesUsers("match-1", "user-1", 0, InviteStatus.ACCEPTED),
                        new MatchesUsers("match-1", "user-2", 1, InviteStatus.INVITED)));
        when(legRepository.getTurnIndex(legId)).thenReturn(0);
        when(scoreCalculator.validateAndBuildVisit(userId, 301, visitRequest, legId))
                .thenReturn(new Visit(
                        "visit-4",
                        legId,
                        userId,
                        visitRequest.score(),
                        false,
                        OffsetDateTime.now()));
        when(progressionHandler.checkResult(any())).thenReturn(ResultScenario.NO_RESULT);

        matchService.processVisitRequest(
                visitRequest, matchId, setId, legId, user.username()
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

        when(userRepository.findByUsername("user1@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> matchService.processVisitRequest(
                visitRequest, matchId, setId, legId, user.username()
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

        when(userRepository.findByUsername("user1@example.com")).thenReturn(Optional.of(user));
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(new Match(matchId, MatchType.FiveO, 3,3,
                OffsetDateTime.now(), null, MatchStatus.ONGOING)));
        when(matchRepository.isValidLegHierarchy(legId, setId, matchId)).thenReturn(false);

        assertThrows(InvalidHierarchyException.class, () -> matchService.processVisitRequest(
                visitRequest, matchId, setId, legId, user.username()
        ));
    }
}