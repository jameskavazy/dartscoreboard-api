package com.jameskavazy.dartscoreboard.match.domain.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.EventPublisher;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.dto.MatchesUserDTO;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Leg;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Set;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.sse.dto.InvitationData;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MatchSetupService {
    private final MatchRepository matchRepository;
    private final SetRepository setRepository;
    private final LegRepository legRepository;
    private final UserRepository userRepository;
//    private final InviteEventEmitter inviteEventEmitter;
    private final EventPublisher eventPublisher;
    private final MatchesUserDTOMapper dtoMapper;


    public MatchSetupService(MatchRepository matchRepository,
                             SetRepository setRepository,
                             LegRepository legRepository,
                             UserRepository userRepository,
                             EventPublisher eventPublisher,
                             MatchesUserDTOMapper dtoMapper) {
        this.matchRepository = matchRepository;
        this.setRepository = setRepository;
        this.legRepository = legRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.dtoMapper = dtoMapper;
    }

    @EventListener
    @Transactional
    public void setupMatchAndSendInvites(MatchRequest matchRequest) {
        Match match = new Match(
                UUID.randomUUID().toString(),
                matchRequest.matchType(),
                matchRequest.raceToLeg(),
                matchRequest.raceToSet(),
                OffsetDateTime.now(),
                null,
                MatchStatus.REQUESTED
        );

        matchRepository.create(match);
        List<MatchesUsers> matchesUsers = buildMatchUsers(matchRequest, match);
        matchesUsers.forEach(matchRepository::createMatchUsers);

        List<MatchesUserDTO> invitedPlayers = matchesUsers.stream()
                .map(dtoMapper::matchesUserToDTO)
                .toList();

        matchesUsers.forEach(mu ->
                eventPublisher.publishInvite(mu.userId(), new InvitationData(match, invitedPlayers))
        );

        Set set = new Set(UUID.randomUUID().toString(), match.matchId(), null, OffsetDateTime.now());
        setRepository.create(set);
        legRepository.create(new Leg(UUID.randomUUID().toString(), match.matchId(), set.setId(), 0, null, OffsetDateTime.now()));
    }

    private List<MatchesUsers> buildMatchUsers(MatchRequest matchRequest, Match match) {
        List<String> userIds = matchRequest.screenNames().stream()
                .map(userRepository::userIdFromScreenName)
                .toList();

        List<MatchesUsers> matchesUsers = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            matchesUsers.add(new MatchesUsers(match.matchId(), userId, i, InviteStatus.INVITED));
        }
        return matchesUsers;
    }
}
