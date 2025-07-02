package com.jameskavazy.dartscoreboard.match.domain;

import com.jameskavazy.dartscoreboard.match.model.visits.Visit;

import java.util.List;

public record VisitResult(
        ResultScenario resultScenario,
        ResultContext resultContext
) {
}
