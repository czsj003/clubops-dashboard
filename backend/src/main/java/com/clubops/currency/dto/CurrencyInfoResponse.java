package com.clubops.currency.dto;

import com.clubops.currency.CurrencyCode;

import java.math.BigDecimal;

public record CurrencyInfoResponse(
        CurrencyCode code,
        String name,
        String symbol,
        BigDecimal rateFromGbp
) {
}