package com.jameskavazy.dartscoreboard.match.visit;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@Transactional
@JdbcTest
@Import({VisitRepository.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VisitRepositoryTest {

    @Autowired
    VisitRepository visitRepository;
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

    @Test
    void shouldReturnEmptyWhenVisitIdDoesNotExist(){
        String invalidId = "invalidID";
        Optional<Visit> result = visitRepository.findVisitById(invalidId);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindVisitWhenValidId(){
        boolean present = visitRepository.findVisitById("visit-1").isPresent();
        assertTrue(present);
    }

    @Test
    void shouldCreateVisit(){
        Visit visit = new Visit(
                "visitId",
               "leg-1",
               "user-1",
                180,
                false
        );

        visitRepository.create(visit);

        assertTrue(visitRepository.findVisitById(visit.visitId()).isPresent());
    }
}