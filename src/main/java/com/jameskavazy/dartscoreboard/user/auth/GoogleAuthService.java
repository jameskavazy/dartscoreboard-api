package com.jameskavazy.dartscoreboard.user.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
@ComponentScan()
public class GoogleAuthService {

//    @Value("${google_client_id}")
    private String WEB_CLIENT_ID = "199671147280-22k2pgmirgujub7vj5tsko68pdptjn36.apps.googleusercontent.com";

    private final NetHttpTransport transport = new NetHttpTransport();
    private final JsonFactory factory = new GsonFactory();

    public GoogleAuthService() {

    }

    public Optional<GoogleIdToken.Payload> verifyToken(String token) throws IOException, GeneralSecurityException, InvalidTokenException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, factory)
                .setAudience(Collections.singletonList(WEB_CLIENT_ID))
                .build();

        try {
            GoogleIdToken idToken = GoogleIdToken.parse(verifier.getJsonFactory(), token);
            if (verifier.verify(idToken)) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                return Optional.ofNullable(payload);
            }
            return Optional.empty();
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new InvalidTokenException("Token parsing failed: " + illegalArgumentException.getMessage(), illegalArgumentException);
        }
    }

}
