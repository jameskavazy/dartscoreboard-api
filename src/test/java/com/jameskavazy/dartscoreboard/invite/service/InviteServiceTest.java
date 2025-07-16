package com.jameskavazy.dartscoreboard.invite.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    MatchRepository matchRepository = mock(MatchRepository.class);
    InviteService inviteService = new InviteService(matchRepository);


    @Test
    void shouldCallUpdateMatchUsers(){

        String userId = "test-user";
        String matchId = "match-id";
        InviteStatus accepted = InviteStatus.ACCEPTED;

        inviteService.updateMatchUserInviteStatus(userId, matchId, accepted);

        verify(matchRepository).updateMatchUserInviteStatus(userId, matchId, accepted);
    }

}