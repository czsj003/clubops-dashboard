package com.clubops.club.dto;

import com.clubops.club.Club;
import com.clubops.club.Country;
import com.clubops.club.FootballLeague;

public record ClubResponse(
        Long id,
        String name,
        Country country,
        FootballLeague league,
        String leagueGroup,
        String season
) {
    public static ClubResponse from(Club club) {
        return new ClubResponse(
                club.getId(),
                club.getName(),
                club.getCountry(),
                club.getLeague(),
                club.getLeagueGroup(),
                club.getSeason()
        );
    }
}
