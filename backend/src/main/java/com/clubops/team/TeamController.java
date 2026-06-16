package com.clubops.team;

import com.clubops.team.dto.TeamResponse;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public List<TeamResponse> getTeams(@AuthenticationPrincipal User user) {
        return teamService.getCurrentUserTeams(user);
    }
}