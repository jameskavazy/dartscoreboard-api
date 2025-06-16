package com.jameskavazy.dartscoreboard.user;

import com.jameskavazy.dartscoreboard.auth.config.AuthConfigProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserRepository.class)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @BeforeAll
    static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthConfigProperties authConfigProperties;

    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldFindUserByEmail(){
        boolean present = userRepository.findByEmail("user1@example.com").isPresent();
        assertTrue(present);
    }

    @Test
    void shouldReturnEmptyIfEmailNotExists(){
        boolean present = userRepository.findByEmail("not_an_email").isPresent();
        assertFalse(present);
    }

    @Test
    void shouldCreateUser(){
        userRepository.create(new User("validUser","fourth-user@email.com", "FourthUser"));

        boolean present = userRepository.findByEmail("fourth-user@email.com").isPresent();
        assertTrue(present);
    }



}