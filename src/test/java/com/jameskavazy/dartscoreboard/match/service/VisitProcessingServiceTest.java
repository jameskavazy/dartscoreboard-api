package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.MatchEventPublisher;
import com.jameskavazy.dartscoreboard.match.domain.service.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.match.exception.InvalidHierarchyException;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchType;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Visit;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.sse.service.MatchEventEmitter;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitProcessingServiceTest {

    @Mock
    VisitRepository visitRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    MatchRepository matchRepository;
    @Mock
    LegRepository legRepository;
//
//    @Mock
//    GameEngine gameEngine;
    @Mock
    SetRepository setRepository;
    @Mock
    ScoreCalculator scoreCalculator;

    @Mock
    MatchEventEmitter matchEventEmitter;

    @InjectMocks
    VisitProcessingService visitProcessingService;

    @Mock
    MatchEventPublisher matchEventPublisher;

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
//        when(gameEngine.checkResult(any())).thenReturn(ResultScenario.NO_RESULT);

        visitProcessingService.processVisitRequest(
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

        assertThrows(UsernameNotFoundException.class, () -> visitProcessingService.processVisitRequest(
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

        assertThrows(InvalidHierarchyException.class, () -> visitProcessingService.processVisitRequest(
                visitRequest, matchId, setId, legId, user.username()
        ));
    }


    @Test
    void shouldValidateVisit_andPublishVisitSubmitEvent(){
        String matchId = "match-1";
        String setId = "set-1";
        String legId = "leg-1";
        String userId = "user-1";
        String userEmail = "user1@example.com";
        VisitRequest visitRequest = new VisitRequest(150);

        User user = new User(userId, userEmail, userEmail);
        Match match =  new Match(matchId, MatchType.FiveO, 1,1,OffsetDateTime.now(), null, MatchStatus.ONGOING);
        Visit visit = new Visit(UUID.randomUUID().toString(), legId, userId, 150, false, OffsetDateTime.now());

        when(userRepository.findByUsername(userEmail)).thenReturn(Optional.of(user));
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.isValidLegHierarchy(legId, setId, matchId)).thenReturn(true);
        when(matchRepository.getMatchUsers(matchId)).thenReturn(List.of(
                new MatchesUsers(matchId, userId, 0, InviteStatus.ACCEPTED))
        );
        when(scoreCalculator.validateAndBuildVisit(eq(userId), anyInt(), eq(visitRequest), eq(legId))).thenReturn(visit);


        visitProcessingService.processVisitRequest(visitRequest, matchId, setId, legId, userEmail);

        verify(matchEventPublisher).publishVisitSubmit(matchId, setId, legId, visit.visitId(), visit.userId());
    }
}