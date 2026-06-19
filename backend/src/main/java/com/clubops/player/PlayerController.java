package com.clubops.player;

import com.clubops.player.dto.PlayerCreateRequest;
import com.clubops.player.dto.PlayerDetailResponse;
import com.clubops.player.dto.PlayerListItemResponse;
import com.clubops.player.dto.PlayerUpdateRequest;
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
    public List<PlayerListItemResponse> getPlayers(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) PlayerPositionType position,
            @RequestParam(required = false) CountryCode nationality
    ) {
        return playerService.getCurrentUserPlayers(
                user,
                search,
                teamId,
                position,
                nationality
        );
    }

    @GetMapping("/{id}")
    public PlayerDetailResponse getPlayerDetail(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        return playerService.getCurrentUserPlayerDetail(user, id);
    }

    @PostMapping
    public PlayerDetailResponse createPlayer(
            @AuthenticationPrincipal User user,
            @RequestBody PlayerCreateRequest request
    ) {
        return playerService.createPlayer(user, request);
    }

    @PutMapping("/{id}")
    public PlayerDetailResponse updatePlayer(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody PlayerUpdateRequest request
    ) {
        return playerService.updatePlayer(user, id, request.player());
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        playerService.deletePlayer(user, id);
    }
}