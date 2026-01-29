package com.jameskavazy.dartscoreboard.invite.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.EventPublisher;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.service.MatchStateAssembler;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InviteService {
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    private final MatchStateAssembler matchStateAssembler;

    public InviteService(MatchRepository matchRepository, UserRepository userRepository, EventPublisher eventPublisher, MatchStateAssembler matchStateAssembler) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.matchStateAssembler = matchStateAssembler;
    }


    public void updateMatchUserInviteStatus(String username, String matchId, InviteStatus inviteStatus) {
        String userId = userRepository.userIdFromUsername(username);
        matchRepository.updateMatchUserInviteStatus(userId, matchId, inviteStatus);

        List<MatchesUsers> matchUsers = matchRepository.getMatchUsers(matchId);
        boolean allAccepted = matchUsers.stream()
                .allMatch(mu -> mu.inviteStatus().equals(InviteStatus.ACCEPTED));

        List<PlayerStateDTO> playerStateDTOS = matchStateAssembler.getPlayerStateDTOS(matchId);

        if (allAccepted) {
            eventPublisher.publishMatchStart(matchId, playerStateDTOS);
        }
    }
}
