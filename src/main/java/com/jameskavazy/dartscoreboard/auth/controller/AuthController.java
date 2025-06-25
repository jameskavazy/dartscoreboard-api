package com.jameskavazy.dartscoreboard.auth.controller;

import com.jameskavazy.dartscoreboard.auth.dto.AuthResponse;
import com.jameskavazy.dartscoreboard.auth.dto.TokenRequest;
import com.jameskavazy.dartscoreboard.auth.dto.AuthResult;
import com.jameskavazy.dartscoreboard.auth.exception.InvalidTokenException;
import com.jameskavazy.dartscoreboard.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth/")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @PostMapping("/google")
    ResponseEntity<?> signIn(@RequestBody TokenRequest request) {
        try {
            Optional<AuthResult> authResult = authService.authenticate(request.token());

            if (authResult.isPresent()) {
                String username = authResult.get().username();
                String jwt = authResult.get().jwt();
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

                return new ResponseEntity<>(
                        new AuthResponse(username),
                        headers,
                        HttpStatus.OK
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
