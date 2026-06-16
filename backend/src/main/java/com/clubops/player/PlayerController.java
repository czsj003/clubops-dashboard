package com.clubops.player;

import com.clubops.player.dto.PlayerDetailResponse;
import com.clubops.player.dto.PlayerListItemResponse;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public List<PlayerListItemResponse> getPlayers(@AuthenticationPrincipal User user) {
        return playerService.getCurrentUserPlayers(user);
    }

    @GetMapping("/{id}")
    public PlayerDetailResponse getPlayerDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        return playerService.getCurrentUserPlayerDetail(user, id);
    }
}