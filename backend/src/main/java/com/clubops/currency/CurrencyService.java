package com.clubops.currency;

import com.clubops.currency.dto.CurrencyInfoResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CurrencyService {

    public CurrencyCode getDefaultCurrency() {
        return CurrencyCode.GBP;
    }

    public List<CurrencyInfoResponse> getAvailableCurrencies() {
        return List.of(
                new CurrencyInfoResponse(CurrencyCode.GBP, "British Pound", "£", new BigDecimal("1.000")),
                new CurrencyInfoResponse(CurrencyCode.EUR, "Euro", "€", new BigDecimal("1.157")),
                new CurrencyInfoResponse(CurrencyCode.TRY, "Turkish Lira", "₺", new BigDecimal("55.217")),
                new CurrencyInfoResponse(CurrencyCode.SAR, "Saudi Riyal", "﷼", new BigDecimal("5.066")),
                new CurrencyInfoResponse(CurrencyCode.CNY, "Chinese Yuan", "¥", new BigDecimal("9.707")),
                new CurrencyInfoResponse(CurrencyCode.USD, "US Dollar", "$", new BigDecimal("1.351")),
                new CurrencyInfoResponse(CurrencyCode.BRL, "Brazilian Real", "R$", new BigDecimal("7.355")),
                new CurrencyInfoResponse(CurrencyCode.ARS, "Argentine Peso", "$", new BigDecimal("1747.871"))
        );
    }

    public BigDecimal convertFromGbp(BigDecimal amountInGbp, CurrencyCode targetCurrency) {
        BigDecimal rate = getRateFromGbp(targetCurrency);
        return amountInGbp.multiply(rate);
    }

    public BigDecimal getRateFromGbp(CurrencyCode currencyCode) {
        return switch (currencyCode) {
            case GBP -> new BigDecimal("1.000");
            case EUR -> new BigDecimal("1.157");
            case TRY -> new BigDecimal("55.217");
            case SAR -> new BigDecimal("5.066");
            case CNY -> new BigDecimal("9.707");
            case USD -> new BigDecimal("1.351");
            case BRL -> new BigDecimal("7.355");
            case ARS -> new BigDecimal("1747.871");
        };
    }

    public String getSymbol(CurrencyCode currencyCode) {
        return switch (currencyCode) {
            case GBP -> "£";
            case EUR -> "€";
            case TRY -> "₺";
            case SAR -> "﷼";
            case CNY -> "¥";
            case USD -> "$";
            case BRL -> "R$";
            case ARS -> "$";
        };
    }
}