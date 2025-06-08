//package com.jameskavazy.dartscoreboard.user.auth;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Collections;
//import java.util.Optional;
//
//@Service
//public class GoogleAuthService {
//
////    @Value("${google_client_id}")
//    private String WEB_CLIENT_ID = "199671147280-22k2pgmirgujub7vj5tsko68pdptjn36.apps.googleusercontent.com";
//
//    private final NetHttpTransport transport;
//    private final JsonFactory factory;
//
//    //TODO inject client library, JSONfactory etc.
//
//    public GoogleAuthService(String webClientId, NetHttpTransport transport, JsonFactory factory) {
//        WEB_CLIENT_ID = webClientId;
//        this.transport = transport;
//        this.factory = factory;
//    }
//
//    public Optional<GoogleIdToken.Payload> verifyToken(String token) throws IOException, GeneralSecurityException {
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, factory)
//                .setAudience(Collections.singletonList(WEB_CLIENT_ID))
//                .build();
//
//        GoogleIdToken idToken = GoogleIdToken.parse(verifier.getJsonFactory(), token);
//        if (verifier.verify(idToken)) {
//            GoogleIdToken.Payload payload = idToken.getPayload();
//            return Optional.ofNullable(payload);
//        }
//        return Optional.empty();
//    }
//
//}
