//package com.jameskavazy.dartscoreboard.auth.service;
//
//import com.jameskavazy.dartscoreboard.auth.config.AuthConfigProperties;
//import com.jameskavazy.dartscoreboard.user.User;
//import com.jameskavazy.dartscoreboard.user.UserPrincipal;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class JwtServiceTest {
//
//    AuthConfigProperties authConfigProperties = new AuthConfigProperties(
//            "testClientId",
//            "testSecret"
//    );
//
//    JwtService jwtService = new JwtService(authConfigProperties);
//
//    @Test
//    void shouldValidateToken_withValidToken(){
//        String token = "testToken";
//        UserDetails userDetails = new UserPrincipal(new User("testId", "test.com", "test.com"));
//
////        when(jwtService.getEmail(token)).thenReturn("test.com");
////
////        jwtService.validateToken(token, userDetails);
//    }
//
//}