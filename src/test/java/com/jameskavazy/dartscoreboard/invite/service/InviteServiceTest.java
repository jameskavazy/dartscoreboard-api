package com.jameskavazy.dartscoreboard.invite.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    MatchRepository matchRepository = mock(MatchRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    InviteService inviteService = new InviteService(matchRepository, userRepository);


    @Test
    void shouldCallUpdateMatchUsers(){

        String username = "example@email.com";
        String matchId = "match-id";
        InviteStatus accepted = InviteStatus.ACCEPTED;
        when(userRepository.userIdFromUsername(username)).thenReturn("testId");

       inviteService.updateMatchUserInviteStatus(username, matchId, accepted);
       verify(matchRepository).updateMatchUserInviteStatus("testId", matchId, accepted);
    }

}