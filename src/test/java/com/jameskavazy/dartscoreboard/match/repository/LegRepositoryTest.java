package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.match.model.legs.Leg;
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
    LegRepository repository;

    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldCreateLeg(){
        Leg leg = new Leg("leg-2", "match-1", "set-1", 1, null, OffsetDateTime.now());

        repository.create(leg);

        int legsCount = repository.countLegsInSet("set-1");
        assertEquals(2, legsCount);
    }

    @Test
    void shouldFindLegById(){
        Leg leg = repository.findLegById("leg-1");
        assertEquals("leg-1", leg.legId());

    }

    @Test
    void shouldCountLegsWonInSet(){
        int i = repository.countLegsWonInSet("user-1", "set-1");
        assertEquals(0,i);
    }

    @Test
    void shouldGetTurnIndex(){
        int turnIndex = repository.getTurnIndex("leg-1");
        assertEquals(0, turnIndex);
    }

    @Test
    void shouldUpdateTurnIndex(){
        int turnIndex = 4;

        repository.updateTurnIndex(4, "leg-1");

        assertEquals(turnIndex, repository.getTurnIndex("leg-1"));
    }

    @Test
    void shouldCountLegsInSet(){
        int i = repository.countLegsInSet("set-1");
        assertEquals(1,i);
    }

    @Test
    void shouldUpdateWinnerId(){
        repository.updateWinnerId("user-2", "leg-1");
        String winnerId = repository.findLegById("leg-1").winnerId();
        assertEquals("user-2", winnerId);
    }
}