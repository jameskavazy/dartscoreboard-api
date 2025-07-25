package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.match.domain.service.ProgressionHandler;
import com.jameskavazy.dartscoreboard.match.domain.service.ScoreCalculator;
import com.jameskavazy.dartscoreboard.match.domain.service.MatchesUserDTOMapper;
import com.jameskavazy.dartscoreboard.sse.impl.InviteEventEmitter;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    VisitRepository visitRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MatchRepository matchRepository;
    @Mock
    LegRepository legRepository;
    @Mock
    ScoreCalculator scoreCalculator;

    @Mock
    SetRepository setRepository;

    @Mock
    ProgressionHandler progressionHandler;

    @Mock
    MatchesUserDTOMapper dtoMapper;

    @Mock
    InviteEventEmitter inviteEventEmitter;

    @InjectMocks
    MatchService matchService;


    // TODO unit test basic CRUD calls



}