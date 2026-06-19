package com.clubops.finance.dto;

import com.clubops.currency.CurrencyCode;

import java.math.BigDecimal;

public record FinanceSummaryResponse(
        CurrencyCode baseCurrency,
        BigDecimal weeklyBaseWage,
        BigDecimal monthlyBaseWage,
        BigDecimal yearlyBaseWage,
        BigDecimal weeklyAutoBonusCost,
        BigDecimal monthlyAutoBonusCost,
        BigDecimal yearlyAutoBonusCost,
        BigDecimal weeklyMaxCost,
        BigDecimal monthlyMaxCost,
        BigDecimal yearlyMaxCost,
        Integer playerCount
) {
}
