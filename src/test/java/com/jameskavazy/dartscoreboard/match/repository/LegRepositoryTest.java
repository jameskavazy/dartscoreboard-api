package com.jameskavazy.dartscoreboard.match.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@JdbcTest
@Import(LegRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LegRepositoryTest {

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
    LegRepository legRepository;

    @Test
    void shouldReturnTrue_withValidHierarchy(){
        boolean validLegHierarchy = legRepository.isValidLegHierarchy("leg-1", "set-1", "match-1");
        assertTrue(validLegHierarchy);
    }

    @Test
    void shouldReturnFalse_withInValidHierarchy_invalidMatch(){
        boolean validLegHierarchy = legRepository.isValidLegHierarchy("leg-1", "set-1", "match-2");
        assertFalse(validLegHierarchy);
    }

    @Test
    void shouldReturnFalse_withInValidHierarchy_invalidSet(){
        boolean validLegHierarchy = legRepository.isValidLegHierarchy("leg-1", "set-2", "match-1");
        assertFalse(validLegHierarchy);
    }

}