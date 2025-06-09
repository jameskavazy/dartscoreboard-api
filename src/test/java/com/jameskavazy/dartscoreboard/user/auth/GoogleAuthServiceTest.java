package com.jameskavazy.dartscoreboard.user.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.auth.service.GoogleAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.security.GeneralSecurityException;

class GoogleAuthServiceTest {


    @Autowired
    GoogleAuthService googleAuthService;

    @MockitoBean
    NetHttpTransport httpTransport;

    @MockitoBean
    JsonFactory jsonFactory;

    @Test
    void shouldAuthenticateWithValidToken() throws InvalidTokenException, GeneralSecurityException, IOException {
        GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .build();

        googleAuthService.authenticate("Test Token");
    }

    @Test
    void shouldNotAuthenticateWithInvalidIdToken() {

    }

    @Test
    void shouldThrowInvalidTokenException(){

    }
}