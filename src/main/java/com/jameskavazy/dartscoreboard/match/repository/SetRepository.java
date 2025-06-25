package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.match.models.sets.Set;
import io.jsonwebtoken.lang.Assert;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SetRepository {

    private final JdbcClient jdbcClient;

    public SetRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Set> getSetsInMatch(String matchId) {
        return jdbcClient.sql("""
                        SELECT *
                        FROM sets
                        WHERE match_id = :matchId
                        """)
                .param("matchId", matchId)
                .query(Set.class)
                .list();
    }

    public int countSetsWonInMatch(String userId, String matchId) {
        return jdbcClient.sql("""
                        SELECT COUNT(set_id)
                        FROM sets
                        WHERE
                            set_winner_id = :userId
                            AND match_id = :matchId
                        """)
                .param("matchId", matchId)
                .param("userId", userId)
                .query(Integer.class)
                .single();
    }

    public void create(Set set) {
        int updated = jdbcClient.sql("""
                        INSERT INTO sets(set_id, match_id, set_winner_id, created_at)
                        values(:setId, :matchId, :setWinnerId, :createdAt)
                        """)
                .param("setId", set.setId())
                .param("matchId", set.matchId())
                .param("setWinnerId", set.setWinnerId())
                .param("createdAt", set.createdAt())
                .update();
        Assert.state(updated == 1, "Could not insert set");
    }

    public void updateWinnerId(String userId, String setId) {
        int updated = jdbcClient.sql("""
                        UPDATE sets
                        SET set_winner_id = :userId
                        WHERE set_id = :setId
                        """)
                .param("userId", userId)
                .param("setId", setId)
                .update();

        Assert.state(updated == 1, "Could not update set winner for set: " + setId);
    }
}
