package com.clubops.value.dto;

import com.clubops.club.Country;
import com.clubops.currency.CurrencyCode;

import java.math.BigDecimal;
import java.util.List;

public record PlayerValueBandBulkRequest(
        Country country,
        CurrencyCode currency,
        List<PlayerValueBandBulkItem> bands
) {
    public record PlayerValueBandBulkItem(
            Integer reputationMin,
            Integer reputationMax,
            BigDecimal baseValue
    ) {
    }
}
