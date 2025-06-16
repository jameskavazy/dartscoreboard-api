package com.jameskavazy.dartscoreboard.match.match;

import com.jameskavazy.dartscoreboard.match.visit.VisitRepository;
import com.jameskavazy.dartscoreboard.match.visit.VisitRequest;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
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
    @Test
    void shouldCreateVisit() {
        VisitRequest visitRequest = new VisitRequest(180);

        String matchId = "match-1";
        String setId = "set-1";
        String legId = "leg-1";
        String userId = "user-1";

        User user = new User(userId, "user1@example.com", "user1@example.com");

        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));


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
}