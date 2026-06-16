package com.clubops.club;

import com.clubops.club.dto.ClubResponse;
import com.clubops.club.dto.ClubUpdateRequest;
import com.clubops.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    public ClubResponse getClub(@AuthenticationPrincipal User user) {
        return clubService.getCurrentUserClub(user);
    }

    @PutMapping
    public ClubResponse updateClub(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ClubUpdateRequest request
    ) {
        return clubService.updateCurrentUserClub(user, request);
    }
}