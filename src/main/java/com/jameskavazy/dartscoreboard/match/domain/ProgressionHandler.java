package com.jameskavazy.dartscoreboard.match.domain;

import org.springframework.stereotype.Component;

@Component
public class ProgressionHandler {

    public ProgressionHandler() {

    }

    public ResultScenario checkResult(MatchContext matchContext){
        if (matchContext.computedScore() != 0) return ResultScenario.NO_LEG_WON;
        if (matchContext.match().raceToLeg() - 1 != matchContext.legsWon()) return ResultScenario.LEG_WON_NO_SET_WON;
        if (matchContext.match().raceToSet() - 1 != matchContext.setsWon()) return ResultScenario.LEG_WON_SET_WON_NO_MATCH_WON;

        return ResultScenario.LEG_WON_SET_WON_MATCH_WON;
    }

    public int increment(int base, int shift, int size) {
        return (base + shift) % size;
    }

}
