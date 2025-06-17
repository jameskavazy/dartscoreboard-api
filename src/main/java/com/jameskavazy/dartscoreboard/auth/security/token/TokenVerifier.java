package com.jameskavazy.dartscoreboard.auth.security.token;

import com.jameskavazy.dartscoreboard.auth.dto.OAuthUser;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface TokenVerifier {
    Optional<OAuthUser> verify(String token) throws InvalidTokenException;
}
