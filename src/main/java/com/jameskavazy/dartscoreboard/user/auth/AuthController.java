package com.jameskavazy.dartscoreboard.user.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth/")
public class AuthController {
    private final GoogleAuthService googleAuthService;
    public AuthController(GoogleAuthService googleAuthService){
        this.googleAuthService = googleAuthService;
    }
    @PostMapping("/google")
    ResponseEntity<?> signIn(@RequestBody TokenRequest tokenRequest) throws GeneralSecurityException, IOException {
        try {
            Optional<GoogleIdToken.Payload> result = googleAuthService.verifyToken(tokenRequest.token());
            if (result.isPresent()) {
                GoogleIdToken.Payload payload = result.get();
                return ResponseEntity.ok(payload.getEmail());
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "TokenExpired",
                            "details", "Your session has expired. Please sign in again."
                    ));
        } catch (InvalidTokenException invalidTokenException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "InvalidToken",
                            "details", invalidTokenException.getMessage())
                    );
        }
    }
}
