package com.clubops.value;

import com.clubops.club.Club;
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

    private static final BigDecimal TALENT_WEIGHT = new BigDecimal("0.85");
    private static final BigDecimal MARKET_WEIGHT = new BigDecimal("0.15");
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
                club.getLeague(),
                reputation
        )
                .map(this::convertBandToGbp)
                .orElseGet(() -> reputationMarketAnchor(reputation));

        BigDecimal currentAbilityValue = currentAbilityValue(
                attributes.getCurrentAbility()
        );
        BigDecimal potentialValue = potentialValue(
                attributes.getCurrentAbility(),
                attributes.getPotentialAbility(),
                player.getAge()
        );

        BigDecimal talentValue = currentAbilityValue
                .add(potentialValue)
                .multiply(ageMarketMultiplier(player.getAge()))
                .multiply(reputationMultiplier(reputation))
                .multiply(positionMultiplier(positions))
                .multiply(versatilityMultiplier(positions));

        BigDecimal finalValue = talentValue.multiply(TALENT_WEIGHT)
                .add(marketAnchor.multiply(MARKET_WEIGHT));

        return roundToNearestThousand(clamp(finalValue));
    }

    private int blendedReputation(PlayerAttribute attributes) {
        double weighted = attributes.getCurrentReputation() * 0.50
                + attributes.getHomeReputation() * 0.30
                + attributes.getWorldReputation() * 0.20;

        return (int) Math.round(weighted);
    }

    private BigDecimal currentAbilityValue(int ca) {
        double normalized = Math.max(ca, 1) / 100.0;
        double value = 7_000_000.0 * Math.pow(normalized, 3.25);
        return BigDecimal.valueOf(value);
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
                * 20_000.0
                * ageDevelopmentFactor
                * potentialQuality
                * caReadiness;

        return BigDecimal.valueOf(value);
    }

    private BigDecimal ageMarketMultiplier(int age) {
        if (age <= 18) return new BigDecimal("1.08");
        if (age <= 21) return new BigDecimal("1.15");
        if (age <= 24) return new BigDecimal("1.12");
        if (age <= 27) return new BigDecimal("1.05");
        if (age <= 29) return BigDecimal.ONE;
        if (age <= 31) return new BigDecimal("0.88");
        if (age <= 33) return new BigDecimal("0.70");
        if (age <= 35) return new BigDecimal("0.52");
        return new BigDecimal("0.34");
    }

    private BigDecimal reputationMultiplier(int reputation) {
        double multiplier = 0.68 + (reputation / 200.0) * 0.62;
        return BigDecimal.valueOf(multiplier);
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
            case STRIKER -> new BigDecimal("1.18");
            case ATTACKING_MIDFIELDER_LEFT,
                 ATTACKING_MIDFIELDER_CENTRAL,
                 ATTACKING_MIDFIELDER_RIGHT -> new BigDecimal("1.12");
            case MIDFIELDER_LEFT,
                 MIDFIELDER_CENTRAL,
                 MIDFIELDER_RIGHT,
                 DEFENSIVE_MIDFIELDER -> new BigDecimal("1.05");
            case WING_BACK_LEFT,
                 WING_BACK_RIGHT -> new BigDecimal("0.98");
            case DEFENDER_LEFT,
                 DEFENDER_CENTRAL,
                 DEFENDER_RIGHT -> new BigDecimal("0.93");
            case GOALKEEPER -> new BigDecimal("0.82");
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
