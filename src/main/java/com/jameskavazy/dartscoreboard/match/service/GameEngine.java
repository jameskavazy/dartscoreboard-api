package com.jameskavazy.dartscoreboard.match.service;


import com.jameskavazy.dartscoreboard.match.domain.aggregate.MatchContext;
import com.jameskavazy.dartscoreboard.match.domain.model.value.ResultContext;
import com.jameskavazy.dartscoreboard.match.domain.model.value.ResultScenario;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Leg;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Set;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class GameEngine {

    private final LegRepository legRepository;
    private final SetRepository setRepository;
    private final MatchRepository matchRepository;

    public GameEngine(LegRepository legRepository, SetRepository setRepository, MatchRepository matchRepository) {
        this.legRepository = legRepository;
        this.setRepository = setRepository;
        this.matchRepository = matchRepository;
    }

    /**
     * handleLegWon handles match logic for when a user has won a Leg in the match. Updates turn, and creates
     * leg and set data within the database.
     *
     * @param matchContext The context contains crucial metadata about the match required for processing the turn
     * @return resultContext The context of the match is returned after processing the required steps
     */
    @Transactional
    public ResultContext handleLegWon(MatchContext matchContext) {
        legRepository.updateWinnerId(matchContext.userId(), matchContext.legId());
        int legsInMatch = legRepository.countLegsInSet(matchContext.setId());
        int setsInMatch = setRepository.getSetsInMatch(matchContext.match().matchId()).size() - 1;

        // Rotate by number of legs in the match. Offset by which set we're in, less 1
        int playersInMatch = matchRepository.getMatchUsers(matchContext.match().matchId()).size();
        int turnIndex = nextPlayerIndex(matchContext, playersInMatch, legsInMatch);
        Leg newLeg = createNewLeg(matchContext.match().matchId(), matchContext.setId(), turnIndex);
        legRepository.create(newLeg);
        return new ResultContext(newLeg.legId(), matchContext.setId());
    }

    /**
     * handleSetWon handles match logic for when a user has won a Set in the match. Updates turn, and creates
     * leg data within the database.
     *
     * @param matchContext The context contains crucial metadata about the match required for processing the turn
     * @return resultContext The context of the match is returned after processing the required steps
     */


    @Transactional
    public ResultContext handleSetWon(MatchContext matchContext){

        legRepository.updateWinnerId(matchContext.userId(), matchContext.legId());
        setRepository.updateWinnerId(matchContext.userId(), matchContext.setId());

        String matchId = matchContext.match().matchId();
        int setsInMatch = setRepository.getSetsInMatch(matchId).size();

        /*
         * Step by setsInMatch - determine who starts the next set can be determined simply from the number of sets played
         */
        int size = matchRepository.getUsersIdsInMatch(matchId).size();
        int turnIndex = nextPlayerIndex(matchContext, size, setsInMatch);

        Set newSet = new Set(UUID.randomUUID().toString(), matchId, null, OffsetDateTime.now());
        setRepository.create(newSet);

        Leg newLeg = createNewLeg(matchId, newSet.setId(), turnIndex);
        legRepository.create(newLeg);

        return new ResultContext(newLeg.legId(), newSet.setId());
    }

    /**
     * handleMatch processes the turn when the match is determined to be won and therefore finished. The turn must
     * be updated with side effects to handle the winning of that leg and set.
     * @param matchContext The context contains crucial metadata about the match required for processing the turn
     * @return ResultContext The context of the match is returned after processing the required steps
     */
    @Transactional
    public ResultContext handleMatchWon(MatchContext matchContext){
        if (matchContext.match().matchStatus() == MatchStatus.COMPLETE) {
            throw new IllegalStateException("Match already complete");
        }

        legRepository.updateWinnerId(matchContext.userId(), matchContext.legId());
        setRepository.updateWinnerId(matchContext.userId(), matchContext.setId());
        Match match = new Match(
                matchContext.match().matchId(),
                matchContext.match().matchType(),
                matchContext.match().raceToLeg(),
                matchContext.match().raceToSet(),
                matchContext.match().createdAt(),
                matchContext.userId(),
                MatchStatus.COMPLETE
        );
        // TODO: Update match elements without creating an entire new object for efficiency
        matchRepository.update(match, match.matchId());
        return new ResultContext(matchContext.legId(), matchContext.setId());
    }

    /**
     * handleNoResult processes the match when there is no milestone outcome. The turn must simply be updated
     * with no other side effects required.
     * @param matchContext The context contains crucial metadata about the match required for processing the turn
     * @return ResultContext The context of the match is returned after processing the required steps
     */
    @Transactional
    public ResultContext handleNoResult(MatchContext matchContext) {
        int currentTurnIndex = legRepository.getTurnIndex(matchContext.legId());
        int nextPlayerIndex = nextPlayerIndex(matchContext, currentTurnIndex, 1);
        legRepository.updateTurnIndex(nextPlayerIndex, matchContext.legId());
        return new ResultContext(matchContext.legId(), matchContext.setId());
    }

//    /**
//     * checkResult determines the ResultScenario used by caller to determine required match state updates
//     * @param matchContext The context contains crucial metadata about the match required for processing the turn
//     * @return ResultScenario the determined result situation enum
//     */
//    public ResultScenario checkResult(MatchContext matchContext) {
//        return progressionHandler.checkResult(matchContext);
//    }

    /**
     * Computes the next player's turn index using simple modular rotation.
     * *
     *  <p>
     *  The caller provides two values:
     *  <strong>currentTurnIndex</strong> – the index of the player whose turn it is currently, and
     *  <strong>step</strong> – the number of "rotations" to apply:
     *  this is typically the count of legs or sets completed, or simply 1 if advancing to the next player
     *  without any special rotation rules. Negative values can be used to move backward.
     *  </p>
     *       *  <p>Formula:</p>
     *  <pre>
     *  nextIndex = (currentTurnIndex + step + playerCount) % playerCount
     *  </pre>
     *       *  <p>Example usage:</p>
     *  <ul>
     *      <li>When a leg is won: step = number of legs completed in the current set, possibly adjusted
     *          by the set index to shift the starting player for the new set.</li>
     *      <li>When simply moving to the next turn in a leg: step = 1 (or -1 to move backward).</li>
     *  </ul>
     *
     * @param ctx Match context containing the players involved.
     * @param currentTurnIndex The current index of the turn.
     * @param step Positive or negative adjustment to the turn.
     * @return The index of the player who should take the next turn.
     */
    public int nextPlayerIndex(MatchContext ctx, int currentTurnIndex, int step) {
        int playerCount = ctx.usersIdsInMatch().size();
        return (currentTurnIndex + step + playerCount) % playerCount;
    }


    private Leg createNewLeg(String matchId, String setId, int turnIndex) {
        return new Leg(UUID.randomUUID().toString(), matchId, setId, turnIndex, null, OffsetDateTime.now());
    }

    ResultScenario checkResult(MatchContext matchContext){
        if (matchContext.computedScore() != 0) return ResultScenario.NO_RESULT;
        if (matchContext.match().raceToLeg() != matchContext.legsWon() + 1) return ResultScenario.LEG_WON;
        if (matchContext.match().raceToSet() == matchContext.setsWon() + 1) return ResultScenario.MATCH_WON;
        return ResultScenario.SET_WON;
    }
}
