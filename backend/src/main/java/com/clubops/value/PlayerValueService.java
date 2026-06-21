package com.clubops.value;

import com.clubops.club.Club;
import com.clubops.currency.CurrencyService;
import com.clubops.player.Player;
import com.clubops.player.PlayerAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PlayerValueService {

    private final PlayerValueBandRepository repository;
    private final CurrencyService currencyService;

    public BigDecimal calculateValueInGbp(
            Player player,
            PlayerAttribute attributes
    ) {
        Club club = player.getClub();
        int reputation = attributes.getWorldReputation();

        PlayerValueBand band = repository.findMatchingBand(
                club.getCountry(),
                club.getLeague(),
                reputation
        ).orElse(null);

        if (band == null) {
            return fallbackValue(attributes);
        }

        BigDecimal baseValueInGbp = band.getBaseValue().divide(
                currencyService.getRateFromGbp(band.getCurrency()),
                2,
                RoundingMode.HALF_UP
        );

        return baseValueInGbp
                .multiply(caMultiplier(attributes.getCurrentAbility()))
                .multiply(paMultiplier(
                        attributes.getCurrentAbility(),
                        attributes.getPotentialAbility()
                ))
                .multiply(ageMultiplier(player.getAge()))
                .multiply(reputationMultiplier(reputation))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal caMultiplier(int ca) {
        if (ca <= 80) return new BigDecimal("0.60");
        if (ca <= 100) return new BigDecimal("0.80");
        if (ca <= 120) return new BigDecimal("1.00");
        if (ca <= 140) return new BigDecimal("1.25");
        if (ca <= 160) return new BigDecimal("1.55");
        if (ca <= 180) return new BigDecimal("1.90");
        return new BigDecimal("2.30");
    }

    private BigDecimal paMultiplier(int ca, int pa) {
        int gap = pa - ca;
        if (gap <= 0) return new BigDecimal("1.00");
        if (gap <= 10) return new BigDecimal("1.05");
        if (gap <= 20) return new BigDecimal("1.12");
        if (gap <= 35) return new BigDecimal("1.22");
        if (gap <= 50) return new BigDecimal("1.35");
        return new BigDecimal("1.50");
    }

    private BigDecimal ageMultiplier(int age) {
        if (age <= 18) return new BigDecimal("1.35");
        if (age <= 21) return new BigDecimal("1.50");
        if (age <= 24) return new BigDecimal("1.35");
        if (age <= 27) return new BigDecimal("1.15");
        if (age <= 30) return new BigDecimal("1.00");
        if (age <= 32) return new BigDecimal("0.80");
        if (age <= 34) return new BigDecimal("0.55");
        return new BigDecimal("0.30");
    }

    private BigDecimal reputationMultiplier(int reputation) {
        if (reputation <= 50) return new BigDecimal("0.85");
        if (reputation <= 100) return new BigDecimal("1.00");
        if (reputation <= 150) return new BigDecimal("1.20");
        return new BigDecimal("1.45");
    }

    private BigDecimal fallbackValue(PlayerAttribute attributes) {
        long base = (long) attributes.getCurrentAbility() * 50_000L;
        long potentialBoost = (long) Math.max(
                attributes.getPotentialAbility() - attributes.getCurrentAbility(),
                0
        ) * 75_000L;

        return BigDecimal.valueOf(base + potentialBoost);
    }
}
