package com.jameskavazy.dartscoreboard.auth.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.JsonFactory;
import com.jameskavazy.dartscoreboard.auth.dto.VerifiedUser;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Component
public class GoogleTokenVerifierWrapper implements TokenVerifier {

    private final GoogleIdTokenVerifier verifier;
    private final JsonFactory jsonFactory;

    public GoogleTokenVerifierWrapper(GoogleIdTokenVerifier verifier, JsonFactory jsonFactory){
        this.verifier = verifier;
        this.jsonFactory = jsonFactory;
    }

    @Override
    public Optional<VerifiedUser> verify(String token) throws InvalidTokenException {
        try {
            GoogleIdToken googleIdToken = GoogleIdToken.parse(jsonFactory, token);
            if (verifier.verify(googleIdToken)) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                String email = payload.getEmail();
                return Optional.of(new VerifiedUser(email));
            }
        } catch (IllegalArgumentException | GeneralSecurityException | IOException e) {
            throw new InvalidTokenException("Token parsing failed: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
