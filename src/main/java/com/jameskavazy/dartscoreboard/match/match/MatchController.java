package com.jameskavazy.dartscoreboard.match.match;

import com.jameskavazy.dartscoreboard.match.visit.VisitRequest;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import jakarta.validation.Valid;
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

    public MatchController(MatchService matchService){
       this.matchService = matchService;
    }

    @GetMapping("")
    List<Match> findAllMatches() {
        return matchService.findAllMatches();
    }

    @GetMapping("/{matchId}")
    Match findMatchById(@PathVariable String matchId){
        Optional<Match> match = matchService.findMatchById(matchId);
        if (match.isEmpty()) {
            throw new MatchNotFoundException();
        }
        return match.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    void createMatch(@Valid @RequestBody MatchRequest matchRequest){
        matchService.createMatch(matchRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{matchId}")
    void updateMatch(@RequestBody Match match, @PathVariable String matchId){
        matchService.updateMatch(match, matchId);
    }

    @PostMapping("/{matchId}/sets/{setId}/legs/{legId}/visits/")
    ResponseEntity<?> createVisit(@PathVariable String matchId,
                                  @PathVariable String setId,
                                  @PathVariable String legId,
                                  @RequestBody VisitRequest visitRequest,
                                  @AuthenticationPrincipal UserDetails userDetails){

        matchService.createVisit(visitRequest, matchId, setId, legId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
