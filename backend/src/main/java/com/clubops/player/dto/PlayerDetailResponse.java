package com.clubops.player.dto;

import com.clubops.player.*;
import com.clubops.team.TeamType;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public record PlayerDetailResponse(
        Long id,

        String firstName,
        String lastName,
        String commonName,
        String fullName,
        String displayName,

        Integer age,
        LocalDate dateOfBirth,

        Race race,
        HairColor hairColor,
        HairLength hairLength,
        SkinTone skinTone,

        Integer heightCm,
        Integer weightKg,

        String birthCity,
        CountryCode birthCountry,
        CountryCode nationality,
        List<PlayerSecondaryNationalityResponse> secondaryNationalities,
        List<PlayerLanguageResponse> languages,

        Long clubId,
        String clubName,
        Long teamId,
        String teamName,
        TeamType teamType,

        Boolean isGoalkeeper,

        List<PlayerPositionResponse> positions,
        PlayerAttributeGroupResponse attributes,

        PlayerPersonality personality,
        MediaHandlingStyle mediaHandlingStyle
) {
    public static PlayerDetailResponse from(
            Player player,
            PlayerAttribute attributes,
            List<PlayerPositionResponse> positions,
            List<PlayerLanguageResponse> languages,
            List<PlayerSecondaryNationalityResponse> secondaryNationalities,
            Boolean isGoalkeeper,
            PlayerPersonality personality,
            MediaHandlingStyle mediaHandlingStyle
    ) {
        return new PlayerDetailResponse(
                player.getId(),

                player.getFirstName(),
                player.getLastName(),
                player.getCommonName(),
                player.getFullName(),
                player.getDisplayName(),

                calculateAge(player.getDateOfBirth()),
                player.getDateOfBirth(),

                player.getRace(),
                player.getHairColor(),
                player.getHairLength(),
                player.getSkinTone(),

                player.getHeightCm(),
                player.getWeightKg(),

                player.getBirthCity(),
                player.getBirthCountry(),
                player.getNationality(),
                secondaryNationalities,
                languages,

                player.getClub().getId(),
                player.getClub().getName(),
                player.getTeam().getId(),
                player.getTeam().getName(),
                player.getTeam().getType(),

                isGoalkeeper,

                positions,
                PlayerAttributeGroupResponse.from(attributes),

                personality,
                mediaHandlingStyle
        );
    }

    private static int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}