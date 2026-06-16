package com.clubops.team.dto;

import com.clubops.team.Team;
import com.clubops.team.TeamType;

public record TeamResponse(
        Long id,
        String name,
        TeamType type,
        Integer displayOrder
) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getType(),
                team.getDisplayOrder()
        );
    }
}