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

    @GetMapping("/{id}")
    Match findMatchById(@PathVariable String id){
        Optional<Match> match = matchRepository.findById(id);
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
    @PutMapping("/{id}")
    void update(@RequestBody Match match, @PathVariable String id){
        matchRepository.update(match, id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        matchRepository.delete(id);
    }
}
