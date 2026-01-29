package com.jameskavazy.dartscoreboard.invite.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.EventPublisher;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Leg;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Set;
import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.service.MatchStateAssembler;
import com.jameskavazy.dartscoreboard.sse.service.MatchEventEmitter;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    MatchRepository matchRepository = mock(MatchRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    LegRepository legRepository = mock(LegRepository.class);
    SetRepository setRepository = mock(SetRepository.class);
    EventPublisher eventPublisher = mock(EventPublisher.class);
    MatchStateAssembler matchStateAssembler = mock(MatchStateAssembler.class);


    InviteService inviteService
            = new InviteService(matchRepository,userRepository,eventPublisher, matchStateAssembler);


    @Test
    void shouldCallUpdateMatchUsers(){

        String username = "example@email.com";
        String matchId = "match-id";
        String setId = "test-set";
        String legId = "test-leg";

        InviteStatus accepted = InviteStatus.ACCEPTED;
        when(userRepository.userIdFromUsername(username)).thenReturn("testId");
        when(setRepository.getSetsInMatch(matchId))
                .thenReturn(List.of(new Set(setId, matchId, null, OffsetDateTime.now())));

        when(legRepository.findActiveLegByMatchId(matchId))
                .thenReturn(new Leg(legId, matchId, setId, 0, null, OffsetDateTime.now()));

        inviteService.updateMatchUserInviteStatus(username, matchId, accepted);


        verify(matchRepository).updateMatchUserInviteStatus("testId", matchId, accepted);
        verify(matchStateAssembler).getPlayerStateDTOS(matchId);
    }

    @Test
    void shouldSendMatchEvent(){

        String username = "example@email.com";
        String matchId = "match-id";
        List<PlayerStateDTO> playerStateDTOS = List.of(new PlayerStateDTO(username, 0, 0, 301, true, false));
        InviteStatus accepted = InviteStatus.ACCEPTED;

        when(matchStateAssembler.getPlayerStateDTOS(matchId)).thenReturn(playerStateDTOS);

        inviteService.updateMatchUserInviteStatus(username, matchId, accepted);
        verify(eventPublisher).publishMatchStart(matchId, playerStateDTOS);
    }

}