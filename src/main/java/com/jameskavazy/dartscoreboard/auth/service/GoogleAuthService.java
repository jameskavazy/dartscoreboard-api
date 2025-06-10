package com.jameskavazy.dartscoreboard.auth.service;

import com.jameskavazy.dartscoreboard.auth.dto.AuthResponse;
import com.jameskavazy.dartscoreboard.auth.dto.VerifiedUser;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import com.jameskavazy.dartscoreboard.auth.security.TokenVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GoogleAuthService {

    private final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);
    private final UserRepository userRepository;
    private final TokenVerifier tokenVerifier;

    public GoogleAuthService(TokenVerifier tokenVerifier, UserRepository userRepository) {
        this.tokenVerifier = tokenVerifier;
        this.userRepository = userRepository;
    }

    public Optional<AuthResponse> authenticate(String token) throws InvalidTokenException {
        Optional<VerifiedUser> userOptional = tokenVerifier.verify(token);

        if (userOptional.isPresent()) {
            String email = userOptional.get().email();
            log.info(email);
            //TODO userRepository.findByEmail(email).orElseGet(()-> userRepository.save(new User(email))
            return Optional.of(new AuthResponse(email));
        }
        return Optional.empty();
    }
}
