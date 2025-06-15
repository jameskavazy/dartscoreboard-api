package com.jameskavazy.dartscoreboard.match.visit;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;


@Repository
public class VisitRepository {

    private final JdbcClient jdbcClient;

    public VisitRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void create(Visit visit){
        int updated = jdbcClient.sql("INSERT INTO visits (visit_id, leg_id, user_id, score, checkout) values (?, ?, ?, ?, ?)")
                .params(List.of(visit.visitId(), visit.legId(), visit.userId(), visit.score(), visit.checkout()))
                .update();
        Assert.state(updated == 1, "Could not insert visit");
    }

    public Optional<Visit> findVisitById(String visitId){
        return jdbcClient.sql("SELECT * FROM visits WHERE visit_id = :visitId")
                .param("visitId", visitId)
                .query(Visit.class)
                .optional();
    }
}
