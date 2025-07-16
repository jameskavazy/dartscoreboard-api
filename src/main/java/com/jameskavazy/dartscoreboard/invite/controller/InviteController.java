package com.jameskavazy.dartscoreboard.invite.controller;

import com.jameskavazy.dartscoreboard.invite.model.InviteStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invites")
public class InviteController {

    @PutMapping("/{matchId}")
    public ResponseEntity<?> respondToInvite(@PathVariable String matchId, @RequestBody InviteStatus inviteStatus){
        return ResponseEntity.noContent().build();
    }
}
