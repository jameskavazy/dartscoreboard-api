package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.EventPublisher;
import com.jameskavazy.dartscoreboard.match.domain.service.MatchSetupService;
import com.jameskavazy.dartscoreboard.match.domain.service.MatchesUserDTOMapper;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Leg;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchType;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Set;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.sse.dto.InvitationData;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchSetupServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    MatchRepository matchRepository;

    @Mock
    EventPublisher eventPublisher;

    @Mock
    SetRepository setRepository;

    @Mock
    LegRepository legRepository;

    @Mock
    MatchesUserDTOMapper dtoMapper;

    @InjectMocks
    MatchSetupService matchSetupService;


    @Test
    void shouldSetupMatchAndSendInvites(){
        MatchRequest matchRequest = new MatchRequest(MatchType.FiveO, 1,1, List.of("user1","user2"));
        when(userRepository.userIdFromScreenName("user1")).thenReturn("user-1");
        when(userRepository.userIdFromScreenName("user2")).thenReturn("user-2");

        matchSetupService.setupMatchAndSendInvites(matchRequest);

        verify(matchRepository).create(argThat(match ->
                match.matchType().equals(MatchType.FiveO) &&
                        match.raceToLeg() == 1 &&
                        match.raceToSet() == 1 &&
                        match.matchStatus().equals(MatchStatus.REQUESTED) &&
                        match.winnerId() == null
        ));

        verify(eventPublisher, times(2))
                .publishInvite(anyString(), any(InvitationData.class));
        verify(setRepository).create(any(Set.class));
        verify(legRepository).create(any(Leg.class));
        verify(matchRepository, times(1)).createMatchUsers(argThat(mu -> mu.userId().equals("user-1")));
        verify(matchRepository, times(1)).createMatchUsers(argThat(mu -> mu.userId().equals("user-2")));

    }
}