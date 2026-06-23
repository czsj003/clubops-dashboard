package com.clubops.value;

import com.clubops.club.Club;
import com.clubops.club.Country;
import com.clubops.currency.CurrencyService;
import com.clubops.player.Player;
import com.clubops.player.PlayerAttribute;
import com.clubops.player.PlayerPositionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlayerValueService {

    private static final BigDecimal MINIMUM_VALUE = new BigDecimal("25000");
    private static final BigDecimal MAXIMUM_VALUE = new BigDecimal("300000000");

    private final PlayerValueBandRepository repository;
    private final CurrencyService currencyService;

    public BigDecimal calculateValueInGbp(
            Player player,
            PlayerAttribute attributes,
            Map<PlayerPositionType, Integer> positions
    ) {
        Club club = player.getClub();
        int reputation = blendedReputation(attributes);

        BigDecimal marketAnchor = repository.findMatchingBand(
                club.getCountry(),
                reputation
        )
                .map(this::convertBandToGbp)
                .orElseGet(() -> reputationMarketAnchor(reputation));

        BigDecimal establishedValue = marketAnchor
                .multiply(currentAbilityMultiplier(attributes.getCurrentAbility()))
                .multiply(establishedAgeMultiplier(player.getAge()));
        BigDecimal potentialValue = potentialValue(
                attributes.getCurrentAbility(),
                attributes.getPotentialAbility(),
                player.getAge()
        ).multiply(countryUpsideFactor(club.getCountry()));

        BigDecimal marketAdjustedValue = establishedValue
                .add(potentialValue)
                .min(establishedValue.multiply(countryUpsideCap(club.getCountry())));

        BigDecimal finalValue = marketAdjustedValue
                .multiply(positionMultiplier(positions))
                .multiply(versatilityMultiplier(positions));

        return roundToNearestThousand(clamp(finalValue));
    }

    private int blendedReputation(PlayerAttribute attributes) {
        double weighted = attributes.getCurrentReputation() * 0.50
                + attributes.getHomeReputation() * 0.30
                + attributes.getWorldReputation() * 0.20;

        return (int) Math.round(weighted);
    }

    private BigDecimal currentAbilityMultiplier(int ca) {
        double multiplier = Math.pow(Math.max(ca, 1) / 130.0, 2.20);
        return BigDecimal.valueOf(Math.max(0.30, Math.min(multiplier, 3.20)));
    }

    private BigDecimal potentialValue(int ca, int pa, int age) {
        int gap = Math.max(pa - ca, 0);

        if (gap == 0 || age >= 30) {
            return BigDecimal.ZERO;
        }

        double ageDevelopmentFactor;
        if (age <= 18) ageDevelopmentFactor = 1.35;
        else if (age <= 21) ageDevelopmentFactor = 1.20;
        else if (age <= 24) ageDevelopmentFactor = 0.90;
        else if (age <= 27) ageDevelopmentFactor = 0.45;
        else ageDevelopmentFactor = 0.15;

        double potentialQuality = 0.55 + (pa / 200.0) * 0.65;
        double caReadiness = 0.55 + (ca / 200.0) * 0.45;
        double value = Math.pow(gap, 1.55)
                * 24_000.0
                * ageDevelopmentFactor
                * potentialQuality
                * caReadiness;

        return BigDecimal.valueOf(value);
    }

    private BigDecimal establishedAgeMultiplier(int age) {
        if (age <= 18) return new BigDecimal("1.05");
        if (age <= 21) return new BigDecimal("1.04");
        if (age <= 24) return new BigDecimal("1.02");
        if (age <= 30) return BigDecimal.ONE;
        if (age <= 32) return new BigDecimal("0.88");
        if (age <= 34) return new BigDecimal("0.70");
        if (age <= 36) return new BigDecimal("0.52");
        return new BigDecimal("0.35");
    }

    private BigDecimal countryUpsideFactor(Country country) {
        return switch (country) {
            case ENGLAND -> new BigDecimal("0.95");
            case SPAIN -> new BigDecimal("0.95");
            case GERMANY -> new BigDecimal("0.90");
            case ITALY, FRANCE -> new BigDecimal("0.85");
            case PORTUGAL -> new BigDecimal("0.75");
            case NETHERLANDS -> new BigDecimal("0.70");
            case BELGIUM, BRAZIL -> new BigDecimal("0.65");
            case ARGENTINA -> new BigDecimal("0.60");
            case TURKEY, SAUDI_ARABIA -> new BigDecimal("0.45");
            case USA -> new BigDecimal("0.40");
            case CHINA -> new BigDecimal("0.25");
        };
    }

    private BigDecimal countryUpsideCap(Country country) {
        return switch (country) {
            case ENGLAND -> new BigDecimal("5.00");
            case SPAIN -> new BigDecimal("4.50");
            case GERMANY -> new BigDecimal("2.60");
            case ITALY -> new BigDecimal("2.40");
            case FRANCE -> new BigDecimal("2.30");
            case PORTUGAL -> new BigDecimal("2.00");
            case NETHERLANDS -> new BigDecimal("1.90");
            case BELGIUM -> new BigDecimal("1.80");
            case BRAZIL -> new BigDecimal("1.75");
            case ARGENTINA -> new BigDecimal("1.70");
            case TURKEY, SAUDI_ARABIA -> new BigDecimal("1.50");
            case USA -> new BigDecimal("1.45");
            case CHINA -> new BigDecimal("1.35");
        };
    }

    private BigDecimal positionMultiplier(
            Map<PlayerPositionType, Integer> positions
    ) {
        return positions.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 20)
                .map(entry -> positionMultiplier(entry.getKey()))
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ONE);
    }

    private BigDecimal positionMultiplier(PlayerPositionType position) {
        return switch (position) {
            case STRIKER -> new BigDecimal("1.10");
            case ATTACKING_MIDFIELDER_LEFT,
                 ATTACKING_MIDFIELDER_CENTRAL,
                 ATTACKING_MIDFIELDER_RIGHT -> new BigDecimal("1.08");
            case MIDFIELDER_LEFT,
                 MIDFIELDER_CENTRAL,
                 MIDFIELDER_RIGHT,
                 DEFENSIVE_MIDFIELDER -> new BigDecimal("1.04");
            case WING_BACK_LEFT,
                 WING_BACK_RIGHT -> new BigDecimal("0.99");
            case DEFENDER_LEFT,
                 DEFENDER_CENTRAL,
                 DEFENDER_RIGHT -> new BigDecimal("0.97");
            case GOALKEEPER -> new BigDecimal("0.88");
        };
    }

    private BigDecimal versatilityMultiplier(
            Map<PlayerPositionType, Integer> positions
    ) {
        long naturalPositions = positions.values()
                .stream()
                .filter(rating -> rating == 20)
                .count();
        long strongSecondaryPositions = positions.values()
                .stream()
                .filter(rating -> rating >= 15 && rating < 20)
                .count();

        double premium = Math.min(
                Math.max(naturalPositions - 1, 0) * 0.025
                        + strongSecondaryPositions * 0.01,
                0.08
        );

        return BigDecimal.valueOf(1.0 + premium);
    }

    private BigDecimal convertBandToGbp(PlayerValueBand band) {
        return band.getBaseValue().divide(
                currencyService.getRateFromGbp(band.getCurrency()),
                2,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal reputationMarketAnchor(int reputation) {
        double normalized = reputation / 200.0;
        return BigDecimal.valueOf(250_000.0 + 22_000_000.0 * normalized * normalized);
    }

    private BigDecimal clamp(BigDecimal value) {
        return value.max(MINIMUM_VALUE).min(MAXIMUM_VALUE);
    }

    private BigDecimal roundToNearestThousand(BigDecimal value) {
        return value.divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000));
    }
}
