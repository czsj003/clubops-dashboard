package com.clubops.player.dto;

import com.clubops.player.CountryCode;
import com.clubops.player.Player;
import com.clubops.currency.CurrencyCode;

import java.time.LocalDate;
import java.time.Period;
import java.math.BigDecimal;

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
        Boolean isGoalkeeper,
        BigDecimal estimatedValueInGbp,
        BigDecimal weeklyWage,
        CurrencyCode wageCurrency
) {
    public static PlayerListItemResponse from(
            Player player,
            Integer currentAbility,
            Integer potentialAbility,
            Boolean isGoalkeeper,
            BigDecimal weeklyWage,
            CurrencyCode wageCurrency
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
                isGoalkeeper,
                player.getEstimatedValueInGbp(),
                weeklyWage,
                wageCurrency
        );
    }

    private static int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}