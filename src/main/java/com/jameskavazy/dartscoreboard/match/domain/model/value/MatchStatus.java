package com.jameskavazy.dartscoreboard.match.domain.model.value;

public enum MatchStatus {

    COMPLETE("Complete"),
    ONGOING("Ongoing"),
    CANCELLED("Cancelled"),
    REQUESTED("Requested");

    MatchStatus(String name) {
    }
}
