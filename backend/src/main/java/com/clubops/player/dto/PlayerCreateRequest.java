package com.clubops.player.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.clubops.contract.PlayerContractType;
import com.clubops.contract.WageDisplayPeriod;
import com.clubops.currency.CurrencyCode;
import com.clubops.player.CountryCode;
import com.clubops.player.HairColor;
import com.clubops.player.HairLength;
import com.clubops.player.LanguageCode;
import com.clubops.player.PlayerPositionType;
import com.clubops.player.Race;
import com.clubops.player.SkinTone;

public record PlayerCreateRequest(
        Long teamId,

        String firstName,
        String lastName,
        String commonName,
        String fullName,

        Race race,
        HairColor hairColor,
        HairLength hairLength,
        SkinTone skinTone,

        Integer heightCm,
        Integer weightKg,
        LocalDate dateOfBirth,
        String birthCity,
        CountryCode birthCountry,
        CountryCode nationality,
        List<CountryCode> secondaryNationalities,

        Map<LanguageCode, Integer> languages,
        Map<PlayerPositionType, Integer> positions,

        Map<String, Integer> attributes,
        String potentialMode,
        String negativePotentialLevel,

        BigDecimal estimatedValueInGbp,

        LocalDate contractStartDate,
        LocalDate contractEndDate,
        PlayerContractType contractType,
        BigDecimal wageAmount,
        CurrencyCode wageCurrency,
        WageDisplayPeriod wageDisplayPeriod,
        Integer squadNumber,
        BigDecimal releaseClauseAmount,
        CurrencyCode releaseClauseCurrency
) {
}
