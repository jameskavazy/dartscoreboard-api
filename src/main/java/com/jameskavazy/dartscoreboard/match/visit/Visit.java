package com.jameskavazy.dartscoreboard.match.visit;

public record Visit(
        String visitId,
        String legId,
        String userId,
        int score,
        boolean checkout
        // TODO might need to class if we're updating checkout?
        //  Altough, we're just inserting to DB so construct new record in mem and UPDATE db???
) {
}
