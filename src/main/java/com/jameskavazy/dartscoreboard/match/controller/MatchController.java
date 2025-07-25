package com.jameskavazy.dartscoreboard.match.controller;

import com.jameskavazy.dartscoreboard.match.domain.model.value.VisitResult;
import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.exception.MatchNotFoundException;

import com.jameskavazy.dartscoreboard.match.service.MatchService;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;

import com.jameskavazy.dartscoreboard.match.service.VisitProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final VisitProcessingService visitProcessingService;

    public MatchController(MatchService matchService, VisitProcessingService visitProcessingService){
        this.matchService = matchService;
        this.visitProcessingService = visitProcessingService;
    }

    @GetMapping("")
    List<Match> findAllMatches() {
        return matchService.findAllMatches();
    }

    @GetMapping("/{matchId}")
    Match findMatchById(@PathVariable String matchId){
        Optional<Match> match = matchService.findMatchById(matchId);
        if (match.isEmpty()) {
            throw new MatchNotFoundException("Could not find match: " + matchId);
        }
        return match.get();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{matchId}")
    void updateMatch(@RequestBody Match match, @PathVariable String matchId){
        matchService.updateMatch(match, matchId);
    }

    @PostMapping("/{matchId}/sets/{setId}/legs/{legId}/visits/")
    ResponseEntity<VisitResult> createVisit(@PathVariable String matchId,
                                  @PathVariable String setId,
                                  @PathVariable String legId,
                                  @RequestBody VisitRequest visitRequest,
                                  @AuthenticationPrincipal UserDetails userDetails){

        VisitResult visitResult =
                visitProcessingService.processVisitRequest(visitRequest, matchId, setId, legId, userDetails.getUsername());

        return new ResponseEntity<>(visitResult, HttpStatus.CREATED);
    }
}
