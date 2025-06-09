package com.jameskavazy.dartscoreboard.auth.controller;

import com.jameskavazy.dartscoreboard.auth.dto.TokenRequest;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.auth.service.GoogleAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            boolean result = googleAuthService.authenticate(request.token());

            if (result) {
                return ResponseEntity.ok().build();
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
