package com.clubops.value.dto;

import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import com.clubops.currency.CurrencyCode;
import com.clubops.value.PlayerValueBand;

import java.math.BigDecimal;

public record PlayerValueBandResponse(
        Long id,
        Country country,
        FootballLeague league,
        Integer reputationMin,
        Integer reputationMax,
        BigDecimal baseValue,
        CurrencyCode currency
) {
    public static PlayerValueBandResponse from(PlayerValueBand band) {
        return new PlayerValueBandResponse(
                band.getId(),
                band.getCountry(),
                band.getLeague(),
                band.getReputationMin(),
                band.getReputationMax(),
                band.getBaseValue(),
                band.getCurrency()
        );
    }
}
