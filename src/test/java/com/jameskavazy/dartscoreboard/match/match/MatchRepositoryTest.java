package com.jameskavazy.dartscoreboard.match.match;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@JdbcTest
@Import(MatchRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MatchRepositoryTest {

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
    MatchRepository repository;

    @BeforeEach
    void setup(){

        repository.create(new Match(
                "first",
                MatchType.FiveO,
                1,
                2,
                OffsetDateTime.parse("2025-06-08T13:12:02.221101+01:00"),
                0
        ));

        repository.create(new Match(
                "second",
                MatchType.ThreeO,
                1,
                2,
                OffsetDateTime.parse("2025-06-08T13:42:02.221101+01:00"),
                0
        ));
    }


    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldFindAllMatches() {
        List<Match> matches = repository.findAll();
        assertEquals(2, matches.size(), "Should find 2 matches");
    }

    @Test
    void shouldFindMatchWithValidId(){
        Optional<Match> result = repository.findById("first");
        assertTrue(result.isPresent());

        Match match = result.get();

        assertEquals("first", match.id());
        assertEquals(MatchType.FiveO, match.type());
        assertEquals(1, match.raceToLeg());
        assertEquals(2, match.raceToSet());
        assertEquals( OffsetDateTime.parse("2025-06-08T13:12:02.221101Z"), match.createdAt());
        assertEquals(0, match.winnerId());
    }

    @Test
    void shouldReturnEmptyWhenMatchIdDoesNotExist(){
        Optional<Match> result = repository.findById("INVALID_ID");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCreateMatch(){
        Match match = new Match("new match", MatchType.SevenO, 1, 1, OffsetDateTime.now(), 0);
        repository.create(match);
        assertTrue(repository.findById("new match").isPresent());
    }

    @Test
    void shouldUpdateMatch(){
        Match match = new Match("new match", MatchType.SevenO, 1, 1, OffsetDateTime.now(), 0);
        repository.update(match, "first");
        Optional<Match> result = repository.findById("first");
        boolean presence = result.isPresent();
        assertTrue(presence);

        Match found = result.get();
        assertEquals("first", found.id());
        assertEquals(MatchType.SevenO, found.type());
    }

    @Test
    void shouldDeleteMatch(){
        repository.delete("first");
        List<Match> matches = repository.findAll();
        assertEquals(1, matches.size());
    }

    @Test
    void shouldReturnCountOfTwo(){
        int count = repository.count();
        assertEquals(2,count);
    }

    @Test
    void shouldReturnMatchesWon(){
        List<Match> matchesWon = repository.findMatchesByWinnerId(0);
        assertEquals(2, matchesWon.size());
    }

}