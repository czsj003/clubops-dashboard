package com.clubops.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class PlayerAbilityResolverTests {

    private final PlayerAbilityResolver resolver = new PlayerAbilityResolver();

    @Test
    void zeroCaAndPaResolveToValidRandomValues() {
        Map<String, Integer> attributes = attributes(0, 0);

        PlayerAbilityResolver.ResolvedAbility result = resolver.resolve(
                LocalDate.now().minusYears(20),
                attributes,
                "RANDOM",
                null
        );

        assertThat(result.currentAbility()).isBetween(1, 200);
        assertThat(result.potentialAbility())
                .isBetween(result.currentAbility(), 200);
        assertThat(attributes.get("currentAbility"))
                .isEqualTo(result.currentAbility());
        assertThat(attributes.get("potentialAbility"))
                .isEqualTo(result.potentialAbility());
    }

    @Test
    void fixedPaBelowCaIsRaisedToCa() {
        Map<String, Integer> attributes = attributes(160, 150);

        PlayerAbilityResolver.ResolvedAbility result = resolver.resolve(
                LocalDate.now().minusYears(20),
                attributes,
                "FIXED",
                null
        );

        assertThat(result.currentAbility()).isEqualTo(160);
        assertThat(result.potentialAbility()).isEqualTo(160);
    }

    @Test
    void validNegativePotentialRespectsRangeAndCa() {
        Map<String, Integer> attributes = attributes(120, 0);

        PlayerAbilityResolver.ResolvedAbility result = resolver.resolve(
                LocalDate.now().minusYears(21),
                attributes,
                "NEGATIVE",
                "-8"
        );

        assertThat(result.potentialAbility()).isBetween(130, 160);
    }

    @Test
    void negativePotentialRejectsPlayersAgedTwentyTwoOrOlder() {
        assertThatThrownBy(() -> resolver.resolve(
                LocalDate.now().minusYears(22),
                attributes(100, 0),
                "NEGATIVE",
                "-8"
        )).hasMessage(
                "Negative potential is only allowed for players under 22"
        );
    }

    @Test
    void negativePotentialRejectsCaAtOrAboveOneHundredSeventy() {
        assertThatThrownBy(() -> resolver.resolve(
                LocalDate.now().minusYears(20),
                attributes(170, 0),
                "NEGATIVE",
                "-10"
        )).hasMessage(
                "Players with CA 170 or higher cannot use negative potential"
        );
    }

    @Test
    void negativePotentialRejectsRangeThatCannotReachCa() {
        assertThatThrownBy(() -> resolver.resolve(
                LocalDate.now().minusYears(20),
                attributes(165, 0),
                "NEGATIVE",
                "-8"
        )).hasMessage(
                "Selected negative potential range cannot produce PA greater than or equal to CA"
        );
    }

    private Map<String, Integer> attributes(int ca, int pa) {
        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("currentAbility", ca);
        attributes.put("potentialAbility", pa);
        return attributes;
    }
}
