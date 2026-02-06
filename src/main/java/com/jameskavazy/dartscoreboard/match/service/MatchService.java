package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.domain.model.entity.Match;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository){
        this.matchRepository = matchRepository;
    }

    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> findMatchById(String matchId) {
        return matchRepository.findById(matchId);
    }

    public void updateMatch(Match match, String matchId) {
        matchRepository.update(match, matchId);
    }

}

