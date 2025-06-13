package com.jameskavazy.dartscoreboard.auth.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.jameskavazy.dartscoreboard.auth.security.GoogleTokenVerifier;
import com.jameskavazy.dartscoreboard.auth.security.TokenVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class GoogleAuthConfig {

    private final AuthConfigProperties authConfigProperties;
    public GoogleAuthConfig(AuthConfigProperties authConfigProperties){
        this.authConfigProperties = authConfigProperties;
    }


    @Bean
    public JsonFactory jsonFactory(){
        return new GsonFactory();
    }

    @Bean
    public NetHttpTransport netHttpTransport(){
        return new NetHttpTransport();
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(JsonFactory jsonFactory, NetHttpTransport netHttpTransport){
        return new GoogleIdTokenVerifier.Builder(netHttpTransport, jsonFactory)
                .setAudience(Collections.singletonList(authConfigProperties.googleClientId()))
                .build();
    }
    @Bean
    public TokenVerifier tokenVerifier(GoogleIdTokenVerifier verifier, JsonFactory jsonFactory){
        return new GoogleTokenVerifier(verifier, jsonFactory);
    }
}
