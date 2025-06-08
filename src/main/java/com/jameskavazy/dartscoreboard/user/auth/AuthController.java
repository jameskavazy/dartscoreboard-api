//package com.jameskavazy.dartscoreboard.user.auth;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/auth/")
//public class AuthController {
//    private final GoogleAuthService googleAuthService;
//    public AuthController(GoogleAuthService googleAuthService){
//        this.googleAuthService = googleAuthService;
//    }
//    @PostMapping("/google")
//    String signIn(@RequestBody String token) throws GeneralSecurityException, IOException {
//        Optional<GoogleIdToken.Payload> result = googleAuthService.verifyToken(token);
//        if (result.isPresent()) {
//            GoogleIdToken.Payload payload = result.get();
//            return payload.getEmail();
//        }
//        return "Not authorised";
//    }
//}
