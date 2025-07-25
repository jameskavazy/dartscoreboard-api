package com.jameskavazy.dartscoreboard.invite.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.sse.impl.MatchEventEmitter;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InviteService {
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final MatchEventEmitter matchEventEmitter;

    public InviteService(MatchRepository matchRepository, UserRepository userRepository, MatchEventEmitter matchEventEmitter) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.matchEventEmitter = matchEventEmitter;
    }


    public void updateMatchUserInviteStatus(String username, String matchId, InviteStatus inviteStatus) {
        String userId = userRepository.userIdFromUsername(username);
        matchRepository.updateMatchUserInviteStatus(userId, matchId, inviteStatus);





        List<MatchesUsers> matchUsers = matchRepository.getMatchUsers(matchId);
        boolean allAccepted = matchUsers.stream()
                .allMatch(mu -> mu.inviteStatus().equals(InviteStatus.ACCEPTED));


        if (allAccepted)
            matchEventEmitter.send(matchId, MatchStatus.ONGOING);

    }
}
