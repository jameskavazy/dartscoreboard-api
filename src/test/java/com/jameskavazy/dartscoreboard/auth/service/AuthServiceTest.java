package com.jameskavazy.dartscoreboard.auth.service;

import com.jameskavazy.dartscoreboard.auth.dto.OAuthUser;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.auth.security.TokenVerifier;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    TokenVerifier tokenVerifier;

    @Mock
    JwtService jwtService;

    @BeforeEach
    void setUp(){
        authService = new AuthService(tokenVerifier, userRepository, jwtService);
    }

    @Test
    void shouldAuthenticateWhenTokenValid() throws InvalidTokenException {
        when(jwtService.generateToken("test")).thenReturn("a token");
        when(tokenVerifier.verify("test")).thenReturn(Optional.of(new OAuthUser("test")));
        when(userRepository.create(new User("test","test"))).thenReturn(new User("test","test"));

        boolean authenticated = authService.authenticate("test").isPresent();
        assertTrue(authenticated);
    }

    @Test
    void shouldNotAuthenticateWhenInvalidToken() throws InvalidTokenException {
        when(tokenVerifier.verify("test")).thenReturn(Optional.empty());
        boolean authenticated = authService.authenticate("test").isPresent();
        assertFalse(authenticated);
    }
}