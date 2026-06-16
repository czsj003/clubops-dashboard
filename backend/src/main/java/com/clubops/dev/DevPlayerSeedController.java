package com.clubops.dev;

import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevPlayerSeedController {

    private final DevPlayerSeedService devPlayerSeedService;

    @PostMapping("/seed-players")
    public String seedPlayers(@AuthenticationPrincipal User user) {
        return devPlayerSeedService.seedPlayersForCurrentUser(user);
    }
}