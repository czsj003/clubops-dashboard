package com.clubops.value.dto;

import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import com.clubops.currency.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PlayerValueBandRequest(
        @NotNull Country country,
        @NotNull FootballLeague league,
        @NotNull @Min(1) @Max(200) Integer reputationMin,
        @NotNull @Min(1) @Max(200) Integer reputationMax,
        @NotNull @DecimalMin("0.00") BigDecimal baseValue,
        @NotNull CurrencyCode currency
) {
}
