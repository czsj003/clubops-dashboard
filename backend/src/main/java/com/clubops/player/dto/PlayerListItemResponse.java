package com.clubops.player.dto;

import com.clubops.player.CountryCode;
import com.clubops.player.Player;

import java.time.LocalDate;
import java.time.Period;

public record PlayerListItemResponse(
        Long id,
        String displayName,
        String fullName,
        Integer age,
        CountryCode nationality,
        Long teamId,
        String teamName,
        Integer currentAbility,
        Integer potentialAbility,
        Boolean isGoalkeeper
) {
    public static PlayerListItemResponse from(
            Player player,
            Integer currentAbility,
            Integer potentialAbility,
            Boolean isGoalkeeper
    ) {
        return new PlayerListItemResponse(
                player.getId(),
                player.getDisplayName(),
                player.getFullName(),
                calculateAge(player.getDateOfBirth()),
                player.getNationality(),
                player.getTeam().getId(),
                player.getTeam().getName(),
                currentAbility,
                potentialAbility,
                isGoalkeeper
        );
    }

    private static int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}