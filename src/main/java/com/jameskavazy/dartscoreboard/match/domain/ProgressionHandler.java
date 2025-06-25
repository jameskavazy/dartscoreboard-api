package com.jameskavazy.dartscoreboard.match.domain;

import org.springframework.stereotype.Component;

@Component
public class ProgressionHandler {

    public ProgressionHandler() {

    }

    public ResultScenario checkResult(MatchContext matchContext){
        if (matchContext.computedScore() != 0) return ResultScenario.NO_RESULT;
        if (matchContext.match().raceToLeg() != matchContext.legsWon() + 1) return ResultScenario.LEG_WON;
        if (matchContext.match().raceToSet() == matchContext.setsWon() + 1) return ResultScenario.MATCH_WON;
        return ResultScenario.SET_WON;
    }
    public int increment(int base, int shift, int size) {
        return (base + shift) % size;
    }

}
