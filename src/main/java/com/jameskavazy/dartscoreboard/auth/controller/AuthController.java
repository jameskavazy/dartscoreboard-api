package com.jameskavazy.dartscoreboard.auth.controller;

import com.jameskavazy.dartscoreboard.auth.dto.AuthResponse;
import com.jameskavazy.dartscoreboard.auth.dto.TokenRequest;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.auth.service.GoogleAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<?> signIn(@RequestBody TokenRequest request) {
        try {
            Optional<AuthResponse> response = googleAuthService.authenticate(request.token());

            if (response.isPresent()) {
                return ResponseEntity.ok(
                        response.get()
                );
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "TokenExpired",
                            "details", "Your session has expired. Please sign in again."
                    ));
        } catch (InvalidTokenException invalidTokenException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "TokenInvalid",
                            "details", invalidTokenException.getMessage())
                    );
        }
    }
}
