package com.clubops.player.dto;

import com.clubops.player.CountryCode;
import com.clubops.player.PlayerSecondaryNationality;

public record PlayerSecondaryNationalityResponse(
        CountryCode countryCode
) {
    public static PlayerSecondaryNationalityResponse from(PlayerSecondaryNationality nationality) {
        return new PlayerSecondaryNationalityResponse(
                nationality.getCountryCode()
        );
    }
}