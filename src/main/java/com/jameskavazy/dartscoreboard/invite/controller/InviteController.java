package com.jameskavazy.dartscoreboard.invite.controller;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.invite.service.InviteService;
import com.jameskavazy.dartscoreboard.match.dto.MatchRequest;
import com.jameskavazy.dartscoreboard.match.domain.service.MatchSetupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invites")
public class InviteController {

    Logger log = LoggerFactory.getLogger(InviteService.class);
    private final InviteService inviteService;
    private final MatchSetupService matchSetupService;

    public InviteController(InviteService inviteService, MatchSetupService matchSetupService) {
        this.inviteService = inviteService;
        this.matchSetupService = matchSetupService;
    }

    @PutMapping("/{matchId}")
    public ResponseEntity<?> respondToInvite(@PathVariable String matchId,
                                             @RequestBody InviteStatus inviteStatus,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        inviteService.updateMatchUserInviteStatus(userDetails.getUsername(), matchId, inviteStatus);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendInvite(@RequestBody MatchRequest matchRequest){
        log.info("send invite endpoint hit!!!");
        matchSetupService.setupMatchAndSendInvites(matchRequest);
    }
}
