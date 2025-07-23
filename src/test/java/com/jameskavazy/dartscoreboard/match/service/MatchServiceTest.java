package com.jameskavazy.dartscoreboard.match.service;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.sse.dto.InvitationData;
import com.jameskavazy.dartscoreboard.sse.impl.InviteEventEmitter;
import com.jameskavazy.dartscoreboard.sse.impl.MatchEventEmitter;
import com.jameskavazy.dartscoreboard.match.domain.*;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.dto.VisitEvent;
import com.jameskavazy.dartscoreboard.match.exception.InvalidHierarchyException;
import com.jameskavazy.dartscoreboard.match.model.legs.Leg;
import com.jameskavazy.dartscoreboard.match.model.matches.*;
import com.jameskavazy.dartscoreboard.match.model.sets.Set;
import com.jameskavazy.dartscoreboard.match.model.visits.Visit;
import com.jameskavazy.dartscoreboard.match.repository.LegRepository;
import com.jameskavazy.dartscoreboard.match.repository.MatchRepository;
import com.jameskavazy.dartscoreboard.match.repository.SetRepository;
import com.jameskavazy.dartscoreboard.match.repository.VisitRepository;
import com.jameskavazy.dartscoreboard.match.dto.VisitRequest;
import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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