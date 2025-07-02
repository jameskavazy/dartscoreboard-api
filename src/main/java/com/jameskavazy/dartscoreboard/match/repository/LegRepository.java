package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.match.model.legs.Leg;
import com.jameskavazy.dartscoreboard.match.model.visits.Visit;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class LegRepository {

    private final JdbcClient jdbcClient;

    public LegRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Leg findLegById(String legId) {
       return jdbcClient.sql("""
                        SELECT *
                        FROM legs
                        WHERE leg_id = :legId
                        """)
                .param("legId", legId)
                .query(Leg.class)
                .single();
    }

    public int countLegsWonInSet(String userId, String setId) {
        return jdbcClient.sql("""
                SELECT COUNT(leg_id)
                FROM legs
                WHERE winner_id = :userId
                    AND set_id = :setId
                """)
                .param("userId", userId)
                .param("setId", setId)
                .query(Integer.class)
                .single();
    }

    public int getTurnIndex(String legId) {
        return jdbcClient.sql("""
                SELECT turn_index
                FROM legs
                WHERE leg_id = :legId
                """)
                .param("legId", legId)
                .query(Integer.class)
                .single();
    }

    public void updateTurnIndex(int nextTurn, String legId) {
        int updated = jdbcClient.sql("""
                        UPDATE legs
                        SET turn_index = :nextTurn
                        WHERE leg_id = :legId
                        """)
                .param("nextTurn", nextTurn)
                .param("legId", legId)
                .update();

        Assert.state(updated == 1, "Could not update turn index");
    }

    public int countLegsInSet(String setId) {
        return jdbcClient.sql("""
                SELECT COUNT(leg_id)
                FROM legs
                WHERE set_id = :setId
                """)
                .param("setId", setId)
                .query(Integer.class)
                .single();
    }

    public void updateWinnerId(String userId, String legId) {
        int updated = jdbcClient.sql("""
                        UPDATE legs
                        SET winner_id = :userId
                        WHERE leg_id = :legId
                        """)
                .param("userId", userId)
                .param("legId", legId)
                .update();

        Assert.state(updated == 1, "Could not update leg winner");
    }

    public void create(Leg leg) {
        int updated = jdbcClient.sql("""
                        INSERT INTO legs(leg_id, set_id, match_id, turn_index, winner_id, created_at)
                        values(:legId,:set_id,:match_id,:turn_index,:winnerId,:createdAt)
                        """)
                .param("legId", leg.legId())
                .param("set_id", leg.setId())
                .param("match_id", leg.matchId())
                .param("turn_index", leg.turnIndex())
                .param("winnerId", leg.winnerId())
                .param("createdAt", leg.createdAt())
                .update();

        Assert.state(updated == 1, "Could not create leg");
    }
}
