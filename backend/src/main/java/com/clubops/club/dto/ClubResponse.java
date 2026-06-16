package com.clubops.club.dto;

import com.clubops.club.Club;
import com.clubops.club.Country;

public record ClubResponse(
        Long id,
        String name,
        Country country,
        String league,
        String season,
        Integer reputation
) {
    public static ClubResponse from(Club club) {
        return new ClubResponse(
                club.getId(),
                club.getName(),
                club.getCountry(),
                club.getLeague(),
                club.getSeason(),
                club.getReputation()
        );
    }
}