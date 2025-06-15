package com.jameskavazy.dartscoreboard.match.match;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository){
        this.matchRepository = matchRepository;
    }

    @GetMapping("/all")
    List<Match> findAll() {
        return matchRepository.findAll();
    }

    @GetMapping("/{matchId}")
    Match findMatchById(@PathVariable String matchId){
        Optional<Match> match = matchRepository.findById(matchId);
        if (match.isEmpty()) {
            throw new MatchNotFoundException();
        }
        return match.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    void create(@Valid @RequestBody Match match){
        matchRepository.create(match);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{matchId}")
    void update(@RequestBody Match match, @PathVariable String matchId){
        matchRepository.update(match, matchId);
    }

}
