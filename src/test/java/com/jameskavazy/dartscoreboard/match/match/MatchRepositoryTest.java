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

//        repository.create(new Match(
//                "first",
//                MatchType.FiveO,
//                1,
//                2,
//                OffsetDateTime.parse("2025-06-08T13:12:02.221101+01:00"),
//                null,
//                Status.ONGOING
//        ));
//
//        repository.create(new Match(
//                "second",
//                MatchType.ThreeO,
//                1,
//                2,
//                OffsetDateTime.parse("2025-06-08T13:42:02.221101+01:00"),
//                null,
//                Status.ONGOING
//        ));
    }


    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldFindAllMatches() {
        List<Match> matches = repository.findAll();
        assertEquals(1, matches.size(), "Should find 1 match");
    }

    @Test
    void shouldFindMatchWithValidId(){
        Optional<Match> result = repository.findById("match-1");
        assertTrue(result.isPresent());

        Match match = result.get();

        assertEquals("match-1", match.matchId());
        assertEquals(MatchType.FiveO.name, match.matchType().name);
        assertEquals(3, match.raceToLeg());
        assertEquals(1, match.raceToSet());
        assertEquals( OffsetDateTime.parse("2025-06-15T20:38:21.414670Z"), match.createdAt());
        assertNull(match.winnerId());
    }

    @Test
    void shouldReturnEmptyWhenMatchIdDoesNotExist(){
        Optional<Match> result = repository.findById("INVALID_ID");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCreateMatch(){
        Match match = new Match("new match", MatchType.SevenO, 1, 1, OffsetDateTime.now(), null, Status.ONGOING);
        repository.create(match);
        assertTrue(repository.findById("new match").isPresent());
    }

    @Test
    void shouldUpdateMatch(){
        Match match = new Match("match-1", MatchType.SevenO, 1, 1, OffsetDateTime.now(), null, Status.ONGOING);
        repository.update(match, "match-1");
        Optional<Match> result = repository.findById("match-1");
        boolean presence = result.isPresent();
        assertTrue(presence);

        Match found = result.get();
        assertEquals("match-1", found.matchId());
        assertEquals(MatchType.SevenO, found.matchType());
    }

    @Test
    void shouldDeleteMatch(){
        repository.delete("match-1");
        List<Match> matches = repository.findAll();
        assertEquals(0, matches.size());
    }

    @Test
    void shouldReturnCountOfOne(){
        int count = repository.count();
        assertEquals(1,count);
    }

    @Test
    void shouldReturnMatchesWon(){
        List<Match> matchesWon = repository.findMatchesByWinnerId("user-1");
        assertEquals(0, matchesWon.size());
    }

    @Test
    void shouldDeleteAll() {
        repository.deleteAll();
        assertEquals(0, repository.findAll().size());
    }
}