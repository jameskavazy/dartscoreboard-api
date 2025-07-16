package com.jameskavazy.dartscoreboard.invite.controller;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import com.jameskavazy.dartscoreboard.invite.service.InviteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invites")
public class InviteController {

    private final InviteService inviteService;

    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @PutMapping("/{matchId}")
    public ResponseEntity<?> respondToInvite(@PathVariable String matchId,
                                             @RequestBody InviteStatus inviteStatus,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        inviteService.updateMatchUserInviteStatus(userDetails.getUsername(), matchId, inviteStatus);
        return ResponseEntity.noContent().build();
    }
}
