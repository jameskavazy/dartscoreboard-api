package com.jameskavazy.dartscoreboard.auth.service;

import com.jameskavazy.dartscoreboard.auth.dto.OAuthUser;
import com.jameskavazy.dartscoreboard.auth.dto.AuthResult;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import com.jameskavazy.dartscoreboard.auth.security.TokenVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final TokenVerifier tokenVerifier;
    private final JwtService jwtService;

    public AuthService(TokenVerifier tokenVerifier, UserRepository userRepository, JwtService jwtService) {
        this.tokenVerifier = tokenVerifier;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public Optional<AuthResult> authenticate(String token) throws InvalidTokenException {
        Optional<OAuthUser> oAuthUserOptional = tokenVerifier.verify(token);

        if (oAuthUserOptional.isPresent()) {
            String email = oAuthUserOptional.get().email();
            log.info(email);
            String jwt = jwtService.generateToken(email);
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> userRepository.create(new User(UUID.randomUUID().toString(), email, email)));

            return Optional.of(new AuthResult(user.email(), jwt));
        }
        return Optional.empty();
    }
}
