package com.clubops.player.dto;

import com.clubops.currency.CurrencyCode;

import java.math.BigDecimal;

public record PlayerValueResponse(
        BigDecimal estimatedValueInGbp,
        CurrencyCode baseCurrency,
        Boolean calculated
) {
}