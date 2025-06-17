package com.jameskavazy.dartscoreboard.auth.service;

import com.jameskavazy.dartscoreboard.auth.config.AuthConfigProperties;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;

@Service
public class JwtService {

    Duration expiration = Duration.ofDays(30);

    private final AuthConfigProperties authConfigProperties;

    public JwtService(AuthConfigProperties authConfigProperties) {
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

     private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(authConfigProperties.jwtSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, UserDetails userDetails){
        return getEmail(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String getEmail(String token) {
        return extractClaims(token).getSubject();
    }
}
