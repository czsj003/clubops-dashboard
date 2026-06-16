package com.clubops.team;

import com.clubops.club.Club;
import com.clubops.club.ClubRepository;
import com.clubops.team.dto.TeamResponse;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final ClubRepository clubRepository;
    private final TeamRepository teamRepository;

    public List<TeamResponse> getCurrentUserTeams(User user) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        return teamRepository.findByClubOrderByDisplayOrderAsc(club)
                .stream()
                .map(TeamResponse::from)
                .toList();
    }
}