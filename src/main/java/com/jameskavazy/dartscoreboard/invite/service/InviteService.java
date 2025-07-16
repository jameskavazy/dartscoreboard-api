package com.jameskavazy.dartscoreboard.invite.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import org.springframework.stereotype.Service;

@Service
public class InviteService {

    private final MatchRepository matchRepository;

    public InviteService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }


    public void updateMatchUserInviteStatus(String userId, String matchId, InviteStatus inviteStatus) {
        matchRepository.updateMatchUserInviteStatus(userId, matchId, inviteStatus);
    }
}
