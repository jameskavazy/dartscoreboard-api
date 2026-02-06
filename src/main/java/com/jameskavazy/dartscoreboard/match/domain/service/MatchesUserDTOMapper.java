package com.jameskavazy.dartscoreboard.match.domain.service;

import com.jameskavazy.dartscoreboard.match.dto.MatchesUserDTO;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class MatchesUserDTOMapper {
    private final UserRepository userRepository;

    public MatchesUserDTOMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public MatchesUserDTO matchesUserToDTO(MatchesUsers matchesUsers){
        String screenName = userRepository.screenNameFromUserId(matchesUsers.userId());

        return new MatchesUserDTO(
                screenName,
                matchesUsers.position(),
                matchesUsers.inviteStatus()
        );
    }
}
