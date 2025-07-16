package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.model.matches.Match;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchType;
import com.jameskavazy.dartscoreboard.match.model.matches.MatchesUsers;
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

    public List<String> getUsersIdsInMatch(String matchId) {
        return jdbcClient.sql("""
                    SELECT user_id
                    FROM matches_users
                    WHERE match_id = :matchId
                    ORDER BY position ASC
                """)
                .param("matchId", matchId)
                .query(String.class)
                .list();
    }

    public List<MatchesUsers> getMatchUsers(String matchId) {
        return jdbcClient.sql("""
                    SELECT * FROM matches_users
                    WHERE match_id = :matchId
                    ORDER BY position ASC
                """)
                .param("matchId", matchId)
                .query(MatchesUsers.class)
                .list();
    }


    public int getStartingScore(String matchId) {
        MatchType matchType = jdbcClient.sql("""
                        SELECT match_type
                        FROM matches
                        WHERE match_id = :matchId
                        """)
                .param("matchId", matchId)
                .query(MatchType.class)
                .single();

        return matchType.startingScore;
    }


    public void createMatchUsers(MatchesUsers matchesUser) {
        int updated = jdbcClient.sql("""
                        INSERT INTO matches_users(match_id, user_id, position, invite_status)
                        VALUES (:matchId, :userId, :position, :inviteStatus)
                        """)
                .param("matchId", matchesUser.matchId())
                .param("userId", matchesUser.userId())
                .param("position", matchesUser.position())
                .param("inviteStatus", matchesUser.inviteStatus().name())
                .update();

        Assert.state(updated == 1, "Could not insert match users");
    }

    public void updateMatchUserInviteStatus(String userId, String matchId, InviteStatus inviteStatus) {
        int updated = jdbcClient.sql("""
                        UPDATE matches_users
                        SET invite_status = :inviteStatus
                        WHERE user_id = :userId AND match_id = :matchId
                        """)
                .param("userId", userId)
                .param("matchId", matchId)
                .param("inviteStatus", inviteStatus.name())
                .update();

        Assert.state(updated == 1, "Could not update user invite status");
    }
}
