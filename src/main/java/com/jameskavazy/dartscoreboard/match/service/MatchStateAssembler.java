package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.domain.model.value.MatchStatus;
import com.jameskavazy.dartscoreboard.match.dto.PlayerStateDTO;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MatchStateAssembler {

    private final LegRepository legRepository;
    private final SetRepository setRepository;
    private final VisitRepository visitRepository;
    private final MatchRepository matchRepository;

    public MatchStateAssembler(LegRepository legRepository, SetRepository setRepository, VisitRepository visitRepository, MatchRepository matchRepository){

        this.legRepository = legRepository;
        this.setRepository = setRepository;
        this.visitRepository = visitRepository;
        this.matchRepository = matchRepository;
    }


    public List<PlayerStateDTO> getPlayerStateDTOS(String matchId) {

        String setId = setRepository.getSetsInMatch(matchId).getFirst().setId();
        String legId = legRepository.findActiveLegByMatchId(matchId).legId();

        return matchRepository.getMatchUsers(matchId).stream()
                .map(user -> {
                    int legsWon = legRepository.countLegsWonInSet(user.userId(), setId);
                    int setsWon = setRepository.countSetsWonInMatch(user.userId(),matchId);
                    int currentScore = visitRepository.extractCurrentScore(user.userId(), legId);
                    boolean isTurn = user.position() == legRepository.getTurnIndex(legId);
                    boolean finished = matchRepository.getMatchById(matchId).matchStatus().equals(MatchStatus.COMPLETE);

                    return new PlayerStateDTO(
                            user.userId(),
                            legsWon,
                            setsWon,
                            currentScore,
                            isTurn,
                            finished
                    );
                })
                .toList();
    }


}
