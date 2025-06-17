package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.match.models.matches.Match;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
public class MatchRepository {

    private final JdbcClient jdbcClient;

    public MatchRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }


    public List<Match> findAll(){
        return jdbcClient.sql("SELECT * FROM matches")
                .query(Match.class)
                .list();
    }
    public Optional<Match> findById(String matchId){
        return jdbcClient.sql("SELECT * FROM matches WHERE match_id = :matchId")
                .param("matchId", matchId)
                .query(Match.class)
                .optional();
    }

    public void create(Match match) {
        jdbcClient.sql("INSERT INTO matches(match_id, created_at, match_type, race_to_leg, race_to_set, winner_id, match_status) values(:matchId,:createdAt,:matchType,:raceToLeg,:raceToSet,:winnerId,:matchStatus)")
                .param("matchId", match.matchId())
                .param("createdAt", match.createdAt())
                .param("matchType", match.matchType().name())
                .param("raceToLeg", match.raceToLeg())
                .param("raceToSet", match.raceToSet())
                .param("winnerId", match.winnerId())
                .param("matchStatus",match.matchStatus().name())
                .update();
    }

    public void update(Match match, String matchId){
        jdbcClient.sql("UPDATE matches SET created_at = :createdAt, match_type = :matchType, race_to_leg = :raceToLeg, race_to_set = :raceToSet, winner_id = :winnerId, match_status = :matchStatus WHERE match_id = :matchId")
                .param("createdAt", match.createdAt())
                .param("matchType", match.matchType().name())
                .param("raceToLeg", match.raceToLeg())
                .param("raceToSet", match.raceToSet())
                .param("winnerId", match.winnerId())
                .param("matchStatus", match.matchStatus().name())
                .param("matchId", matchId)
                .update();
    }

    public void delete(String matchId){
        int updated = jdbcClient.sql("DELETE FROM matches WHERE match_id = :matchId")
                .param("matchId", matchId)
                .update();

        Assert.state(updated == 1, "Failed to delete match " + matchId);
    }

   public int count(){
        return jdbcClient.sql("SELECT COUNT(match_id) FROM matches").query(Integer.class).single();
   }

   public List<Match> findMatchesByWinnerId(String winnerId){
        return jdbcClient.sql("SELECT * FROM matches WHERE winner_id = :winner_id")
                .param("winner_id", winnerId)
                .query(Match.class)
                .list();
   }

   public void deleteAll(){
       jdbcClient.sql("DELETE FROM matches")
               .update();
   }
}
