package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.match.domain.PlayerState;
import com.jameskavazy.dartscoreboard.match.model.visits.Visit;
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
        int updated = jdbcClient.sql("INSERT INTO visits (visit_id, leg_id, user_id, score, checkout, created_at) values (?, ?, ?, ?, ?, ?)")
                .params(List.of(visit.visitId(), visit.legId(), visit.userId(), visit.score(), visit.checkout(), visit.createdAt()))
                .update();
        Assert.state(updated == 1, "Could not insert visit");
    }

    public Optional<Visit> findVisitById(String visitId){
        return jdbcClient.sql("SELECT * FROM visits WHERE visit_id = :visitId")
                .param("visitId", visitId)
                .query(Visit.class)
                .optional();
    }


    public void deleteLatestVisit(String legId) {
        String query = """
                DELETE FROM visits
                WHERE visit_id = (
                    SELECT visit_id
                    FROM visits
                    WHERE leg_id = :legId
                    ORDER BY created_at DESC
                    LIMIT 1
                )
                """;
        int updated = jdbcClient.sql(query)
                .param("legId", legId)
                .update();
        Assert.state(updated == 1, "Could not delete visit");
    }

    public List<Visit> findAll() {
        return jdbcClient.sql("SELECT * FROM visits")
                .query(Visit.class)
                .list();
    }

    public int extractCurrentScore(String userId, String legId) {
        return jdbcClient.sql("""
                SELECT COALESCE(SUM(score),0)
                FROM visits
                WHERE leg_id = :legId
                    AND user_id = :userId
                """).param("legId", legId)
                .param("userId", userId)
                .query(Integer.class)
                .single();
    }

    public List<Visit> visitsInLeg(String legId) {
        return jdbcClient.sql("""
                SELECT *
                FROM visits
                WHERE leg_id = :legId
                """)
                .param("legId", legId)
                .query(Visit.class)
                .list();
    }

    public List<PlayerState> getMatchData(String legId){
        return jdbcClient.sql("""
                SELECT
                    v.user_id,
                    COALESCE(SUM(v.score), 0) AS total_score,
                    (mu.position = l.turn_index) AS turn,
                    CASE m.match_type
                    	WHEN 'FiveO' then 501
                    	WHEN 'ThreeO' then 301
                    	WHEN 'SevenO' then 170
                    	ELSE null
                    END AS starting_score
                FROM visits v
                JOIN legs l ON v.leg_id = l.leg_id
                JOIN matches_users mu ON mu.user_id = v.user_id AND mu.match_id = l.match_id
                JOIN matches m on m.match_id = l.match_id
                WHERE v.leg_id = :legId
                GROUP BY v.user_id, mu.position, l.turn_index, m.match_type;
                """)
                .param("legId", legId)
                .query(PlayerState.class)
                .list();
    }


}
