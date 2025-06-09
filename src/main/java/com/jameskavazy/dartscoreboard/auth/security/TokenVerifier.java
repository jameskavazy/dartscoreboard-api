package com.jameskavazy.dartscoreboard.auth.security;

import com.jameskavazy.dartscoreboard.auth.dto.VerifiedUser;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface TokenVerifier {
    Optional<VerifiedUser> verify(String token) throws InvalidTokenException;
}
