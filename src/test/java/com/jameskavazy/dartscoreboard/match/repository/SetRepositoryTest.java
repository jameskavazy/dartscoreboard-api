package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.match.model.sets.Set;
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

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@JdbcTest
@Import(SetRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SetRepositoryTest {
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
    SetRepository repository;

    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldGetAllSetsInMatch(){
        List<Set> setsInMatch = repository.getSetsInMatch("match-1");
        assertEquals(1, setsInMatch.size());
    }

    @Test
    void shouldCountSetsWonInMatch(){
        int i = repository.countSetsWonInMatch("user-1", "match-1");

        assertEquals(0, i);

        repository.create(new Set(
                "set-2", "match-1","user-1", OffsetDateTime.now()
        ));

        int won = repository.countSetsWonInMatch("user-1", "match-1");
        assertEquals(1, won);
    }

    @Test
    void shouldCreateSet(){
        repository.create(
                new Set(
                "set-2", "match-1","user-1", OffsetDateTime.now()
        ));

        assertEquals(2, repository.getSetsInMatch("match-1").size());
    }

    @Test
    void shouldUpdateWinnerId(){
        repository.updateWinnerId("user-1", "set-1");

        int i = repository.countSetsWonInMatch("user-1", "match-1");

        assertEquals(1, i);
    }

}