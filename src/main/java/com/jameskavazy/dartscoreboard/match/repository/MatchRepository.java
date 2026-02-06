package com.jameskavazy.dartscoreboard.match.repository;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchType;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.MatchesUsers;
import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
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

   public Match getMatchById(String matchId){
        return jdbcClient.sql("""
                SELECT
                    *
                FROM
                    matches
                WHERE
                    match_id = :matchId
                """)
                .param("matchId", matchId)
                .query(Match.class)
                .single();
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

    public List<PlayerStateDTO> getLatestStateForMatch(String matchId){

        return jdbcClient.sql("""
                
                WITH current_leg AS (
                    SELECT leg_id, turn_index
                    FROM legs
                    WHERE match_id = :matchId
                    ORDER BY created_at DESC
                    LIMIT 1
                ),

                legs_in_match as (
                    select
                        leg_id
                    from legs l
                    where l.match_id = :matchId
                ),

                legs_counts AS (
                    SELECT winner_id, COUNT(*) as legs_won
                    FROM legs
                    WHERE match_id = :matchId AND winner_id IS NOT NULL
                    GROUP BY winner_id
                ),
                sets_counts AS (
                    SELECT set_winner_id, COUNT(*) as sets_won
                    FROM sets
                    WHERE match_id = :matchId AND set_winner_id IS NOT NULL
                    GROUP BY set_winner_id
                ),
                stats_summary AS (
                    select
                        v.user_id,
                        sum(coalesce(v.score, 0)) as total_score,
                        COUNT(*) as total_visits
                    from
                        visits v
                    where
                        v.leg_id in (select leg_id from legs_in_match)
                    group by
                        v.user_id
                )
                SELECT
                    mu.user_id,
                    coalesce(lc.legs_won, 0) AS legs_won,
                    coalesce(sc.sets_won, 0) AS sets_won,
                    CASE m.match_type
                        WHEN 'FiveO'  THEN 501 - SUM(COALESCE(v.score, 0))
                        WHEN 'ThreeO' THEN 301 - SUM(COALESCE(v.score, 0))
                        WHEN 'SevenO' THEN 170 - SUM(COALESCE(v.score, 0))
                    END AS score,
                    (mu.position = cl.turn_index) AS is_turn,
                    CASE
                        WHEN m.match_status = 'ONGOING' THEN FALSE
                        ELSE TRUE
                    END AS finished,
                    ROUND (
                        SUM(CAST(coalesce(v.score, 0) AS DECIMAL)) / CAST(COUNT(*) AS DECIMAL),
                        2
                    ) AS leg_average,
                    ROUND(CAST(ss.total_score AS DECIMAL) / CAST(ss.total_visits AS DECIMAL), 2)AS match_average
                
                FROM matches_users mu
                JOIN matches m ON m.match_id = mu.match_id
                CROSS JOIN current_leg cl
                LEFT JOIN legs_counts lc ON mu.user_id = lc.winner_id
                LEFT JOIN sets_counts sc ON mu.user_id = sc.set_winner_id
                LEFT JOIN visits v ON v.user_id = mu.user_id AND v.leg_id = cl.leg_id
                LEFT join stats_summary ss on ss.user_id = mu.user_id
                WHERE mu.match_id = :matchId
                GROUP BY
                    mu.user_id,
                    lc.legs_won,
                    sc.sets_won,
                    m.match_type,
                    mu.position,
                    cl.turn_index,
                    m.match_status,
                    ss.total_score,
                    ss.total_visits
                """)
                .param("matchId", matchId)
                .query(PlayerStateDTO.class)
                .list();
    }
}
