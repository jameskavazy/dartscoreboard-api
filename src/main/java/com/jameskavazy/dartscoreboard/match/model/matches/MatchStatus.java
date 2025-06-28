package com.jameskavazy.dartscoreboard.match.model.matches;

public enum MatchStatus {

    COMPLETE("Complete"),
    ONGOING("Ongoing"),
    CANCELLED("Cancelled"),
    REQUESTED("Requested");

    MatchStatus(String name) {
    }
}
