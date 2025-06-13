package com.jameskavazy.dartscoreboard.auth.service;

import com.jameskavazy.dartscoreboard.auth.config.AuthConfigProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.*;

@Service
public class JWTService {

    Duration expiration = Duration.ofHours(24);

    private final AuthConfigProperties authConfigProperties;

    public JWTService(AuthConfigProperties authConfigProperties) {
        this.authConfigProperties = authConfigProperties;

    }
    public String generateToken(String email){
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .and()
                .signWith(getKey())
                .compact();

    }

    private Key getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(authConfigProperties.jwtSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
