package com.jameskavazy.dartscoreboard.auth.service;

import com.jameskavazy.dartscoreboard.auth.dto.VerifiedUser;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.auth.security.GoogleTokenVerifierWrapper;
import com.jameskavazy.dartscoreboard.auth.security.TokenVerifier;
import com.jameskavazy.dartscoreboard.match.match.MatchRepository;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

    GoogleAuthService googleAuthService;

    @Mock
    UserRepository userRepository;

    @Mock
    TokenVerifier tokenVerifier;

    @BeforeEach
    void setUp(){
        googleAuthService = new GoogleAuthService(tokenVerifier, userRepository);
    }

    @Test
    void shouldAuthenticateWhenTokenValid() throws InvalidTokenException {
        when(tokenVerifier.verify("test")).thenReturn(Optional.of(new VerifiedUser("test")));
        boolean authenticated = googleAuthService.authenticate("test").isPresent();
        assertTrue(authenticated);
    }

    @Test
    void shouldNotAuthenticateWhenInvalidToken() throws InvalidTokenException {
        when(tokenVerifier.verify("test")).thenReturn(Optional.empty());
        boolean authenticated = googleAuthService.authenticate("test").isPresent();
        assertFalse(authenticated);
    }
}