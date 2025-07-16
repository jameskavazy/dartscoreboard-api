package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.model.matches.*;
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
        assertEquals(1, match.raceToLeg());
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
        Match match = new Match("new match", MatchType.SevenO, 1, 1, OffsetDateTime.now(), null, MatchStatus.ONGOING);
        repository.create(match);
        assertTrue(repository.findById("new match").isPresent());
    }

    @Test
    void shouldUpdateMatch(){
        Match match = new Match("match-1", MatchType.SevenO, 1, 1, OffsetDateTime.now(), null, MatchStatus.ONGOING);
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

        @Test
    void shouldReturnTrue_withValidHierarchy(){
        boolean validLegHierarchy = repository.isValidLegHierarchy("leg-1", "set-1", "match-1");
        assertTrue(validLegHierarchy);
    }

    @Test
    void shouldReturnFalse_withInValidHierarchy_invalidMatch(){
        boolean validLegHierarchy = repository.isValidLegHierarchy("leg-1", "set-1", "match-2");
        assertFalse(validLegHierarchy);
    }

    @Test
    void shouldReturnFalse_withInValidHierarchy_invalidSet(){
        boolean validLegHierarchy = repository.isValidLegHierarchy("leg-1", "set-2", "match-1");
        assertFalse(validLegHierarchy);
    }

    @Test
    void shouldReturnStartingScore(){
        int startingScore = repository.getStartingScore("match-1");
        assertEquals(501, startingScore);
    }

    @Test
    void shouldInsertMatchUsers(){
        repository.create(
                new Match("match-2", MatchType.SevenO, 1,1,OffsetDateTime.now(), null, MatchStatus.REQUESTED)
        );
        MatchesUsers matchesUser = new MatchesUsers("match-2", "user-1", 0, InviteStatus.INVITED);
        repository.createMatchUsers(matchesUser);
        List<MatchesUsers> matchUsers = repository.getMatchUsers("match-2");
        assertEquals(1, matchUsers.size());
        assertEquals("user-1", matchUsers.get(0).userId());
    }

    @Test
    void shouldUpdateMatchUserInviteStatusToDeclined(){
        repository.updateMatchUserInviteStatus("user-3", "match-1", InviteStatus.DECLINED);
        MatchesUsers matchesUser = repository.getMatchUsers("match-1").get(2);
        assertEquals(InviteStatus.DECLINED, matchesUser.inviteStatus());
    }

}