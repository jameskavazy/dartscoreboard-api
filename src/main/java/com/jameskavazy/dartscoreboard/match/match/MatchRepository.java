package com.jameskavazy.dartscoreboard.match.match;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
public class MatchRepository {

//    private static final Logger log = LoggerFactory.getLogger(MatchRepository.class);
    private final JdbcClient jdbcClient;

    public MatchRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }


    List<Match> findAll(){
        return jdbcClient.sql("SELECT * FROM matches")
                .query(Match.class)
                .list();
    }
    Optional<Match> findById(String id){
        return jdbcClient.sql("SELECT * FROM matches WHERE id = :id")
                .param("id", id)
                .query(Match.class)
                .optional();
    }

    public void create(Match match) {
        int updated = jdbcClient.sql("INSERT INTO matches(id, created_at, type, race_to_leg, race_to_set, winner_id) values(?,?,?,?,?,?)")
                .params(List.of(match.id(), match.createdAt(), match.type().name(), match.raceToLeg(), match.raceToSet(), match.winnerId()))
                .update();

        Assert.state(updated == 1, "Failed to create match " + match.id());
    }

    void update(Match match, String id){
        int updated = jdbcClient.sql("UPDATE matches SET created_at = ?, type = ?, race_to_leg = ?, race_to_set = ?, winner_id = ? WHERE id = ?")
                .params(List.of(match.createdAt(), match.type().name(), match.raceToLeg(), match.raceToSet(), match.winnerId(), id))
                .update();

        Assert.state(updated == 1, "Failed to update match " + match.id());
    }

    void delete(String id){
        int updated = jdbcClient.sql("DELETE FROM matches WHERE id = :id")
                .param("id", id)
                .update();

        Assert.state(updated == 1, "Failed to delete match " + id);
    }

   public int count(){
        return jdbcClient.sql("SELECT COUNT(id) FROM matches").query(Integer.class).single();
   }

   public List<Match> findMatchesByWinnerId(int winnerId){
        return jdbcClient.sql("SELECT * FROM matches WHERE winner_id = :winner_id")
                .param("winner_id", winnerId)
                .query(Match.class)
                .list();
   }
}
