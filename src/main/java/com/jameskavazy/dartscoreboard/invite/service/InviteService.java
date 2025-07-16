package com.jameskavazy.dartscoreboard.invite.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class InviteService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public InviteService(MatchRepository matchRepository, UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }


    public void updateMatchUserInviteStatus(String username, String matchId, InviteStatus inviteStatus) {
        String userId = userRepository.userIdFromUsername(username);
        matchRepository.updateMatchUserInviteStatus(userId, matchId, inviteStatus);
    }
}
