package com.jameskavazy.dartscoreboard.match.repository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class LegRepository {

    private final JdbcClient jdbcClient;

    public LegRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public boolean isValidLegHierarchy(String legId, String setId, String matchId){
        Integer count = jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM legs l
                        JOIN sets s ON l.set_id = s.set_id
                        JOIN matches m ON s.match_id = m.match_id
                        WHERE l.leg_id = :legId
                            AND s.set_id = :setId
                            AND m.match_id = :matchId
                        """)
                .param("legId", legId)
                .param("setId", setId)
                .param("matchId", matchId)
                .query(Integer.class)
                .single();

        return count == 1;
    }
}
