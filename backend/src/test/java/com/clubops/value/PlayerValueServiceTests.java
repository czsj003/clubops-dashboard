package com.clubops.value;

import com.clubops.club.Club;
import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import com.clubops.currency.CurrencyCode;
import com.clubops.currency.CurrencyService;
import com.clubops.player.Player;
import com.clubops.player.PlayerAttribute;
import com.clubops.player.PlayerPositionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerValueServiceTests {

    private final PlayerValueBandRepository repository =
            mock(PlayerValueBandRepository.class);
    private final PlayerValueService service =
            new PlayerValueService(repository, new CurrencyService());

    @Test
    void maturePlayerWithoutPotentialStaysCloseToDatabaseBand() {
        Player player = player(30);
        PlayerAttribute attributes = attributes(130, 130, 130, 130, 130);
        PlayerValueBand band = band(BigDecimal.valueOf(5_000_000));

        when(repository.findMatchingBand(
                Country.ENGLAND,
                130
        )).thenReturn(Optional.of(band));

        BigDecimal value = service.calculateValueInGbp(
                player,
                attributes,
                Map.of(PlayerPositionType.MIDFIELDER_CENTRAL, 20)
        );

        assertThat(value)
                .isBetween(
                        BigDecimal.valueOf(5_000_000),
                        BigDecimal.valueOf(5_500_000)
                );
    }

    @Test
    void eliteTeenageProspectAddsLargePotentialPremiumToDatabaseBand() {
        Player player = player(18);
        PlayerAttribute attributes = attributes(130, 185, 130, 130, 100);
        PlayerValueBand band = band(BigDecimal.valueOf(5_000_000));

        when(repository.findMatchingBand(
                Country.ENGLAND,
                124
        )).thenReturn(Optional.of(band));

        BigDecimal value = service.calculateValueInGbp(
                player,
                attributes,
                Map.of(PlayerPositionType.STRIKER, 20)
        );

        assertThat(value)
                .isBetween(
                        BigDecimal.valueOf(20_000_000),
                        BigDecimal.valueOf(25_000_000)
                );
    }

    @Test
    void englandProspectRemainsNearExpectedTwentyFiveMillionValue() {
        Player player = player(18, Country.ENGLAND);
        PlayerAttribute attributes = attributes(130, 185, 130, 130, 100);
        PlayerValueBand band = band(
                Country.ENGLAND,
                BigDecimal.valueOf(8_000_000),
                CurrencyCode.GBP
        );

        when(repository.findMatchingBand(Country.ENGLAND, 124))
                .thenReturn(Optional.of(band));

        BigDecimal value = service.calculateValueInGbp(
                player,
                attributes,
                Map.of(PlayerPositionType.STRIKER, 20)
        );

        assertThat(value).isBetween(
                BigDecimal.valueOf(23_000_000),
                BigDecimal.valueOf(27_000_000)
        );
    }

    @Test
    void chinaMarketCapPreventsExtremeProspectInflation() {
        Player player = player(18, Country.CHINA);
        PlayerAttribute attributes = attributes(130, 185, 130, 130, 100);
        PlayerValueBand band = band(
                Country.CHINA,
                BigDecimal.valueOf(20_000_000),
                CurrencyCode.CNY
        );

        when(repository.findMatchingBand(Country.CHINA, 124))
                .thenReturn(Optional.of(band));

        BigDecimal valueInGbp = service.calculateValueInGbp(
                player,
                attributes,
                Map.of(PlayerPositionType.STRIKER, 20)
        );
        BigDecimal valueInCny = valueInGbp.multiply(new BigDecimal("9.707"));

        assertThat(valueInCny).isBetween(
                BigDecimal.valueOf(28_000_000),
                BigDecimal.valueOf(35_000_000)
        );
    }

    @Test
    void allThreeReputationValuesAffectTheResult() {
        when(repository.findMatchingBand(
                Country.ENGLAND,
                100
        )).thenReturn(Optional.empty());
        when(repository.findMatchingBand(
                Country.ENGLAND,
                65
        )).thenReturn(Optional.empty());

        BigDecimal establishedValue = service.calculateValueInGbp(
                player(24),
                attributes(125, 135, 120, 100, 80),
                Map.of(PlayerPositionType.MIDFIELDER_CENTRAL, 20)
        );
        BigDecimal lowDomesticValue = service.calculateValueInGbp(
                player(24),
                attributes(125, 135, 50, 50, 100),
                Map.of(PlayerPositionType.MIDFIELDER_CENTRAL, 20)
        );

        assertThat(establishedValue).isGreaterThan(lowDomesticValue);
    }

    @Test
    void positionOrderIsStrikerThenMidfielderThenDefenderThenGoalkeeper() {
        when(repository.findMatchingBand(
                Country.ENGLAND,
                100
        )).thenReturn(Optional.empty());

        Player player = player(25);
        PlayerAttribute attributes = attributes(120, 130, 100, 100, 100);

        BigDecimal striker = value(player, attributes, PlayerPositionType.STRIKER);
        BigDecimal midfielder = value(
                player,
                attributes,
                PlayerPositionType.MIDFIELDER_CENTRAL
        );
        BigDecimal defender = value(
                player,
                attributes,
                PlayerPositionType.DEFENDER_CENTRAL
        );
        BigDecimal goalkeeper = value(
                player,
                attributes,
                PlayerPositionType.GOALKEEPER
        );

        assertThat(striker).isGreaterThan(midfielder);
        assertThat(midfielder).isGreaterThan(defender);
        assertThat(defender).isGreaterThan(goalkeeper);
    }

    @Test
    void extraNaturalAndStrongPositionsAddOnlyACappedPremium() {
        when(repository.findMatchingBand(
                Country.ENGLAND,
                100
        )).thenReturn(Optional.empty());

        Player player = player(25);
        PlayerAttribute attributes = attributes(120, 130, 100, 100, 100);

        BigDecimal singlePosition = service.calculateValueInGbp(
                player,
                attributes,
                Map.of(PlayerPositionType.STRIKER, 20)
        );
        BigDecimal versatile = service.calculateValueInGbp(
                player,
                attributes,
                Map.of(
                        PlayerPositionType.STRIKER, 20,
                        PlayerPositionType.ATTACKING_MIDFIELDER_CENTRAL, 20,
                        PlayerPositionType.ATTACKING_MIDFIELDER_LEFT, 17
                )
        );

        assertThat(versatile).isGreaterThan(singlePosition);
        assertThat(versatile)
                .isLessThan(singlePosition.multiply(new BigDecimal("1.10")));
    }

    @Test
    void usesReputationDerivedMarketAnchorWhenNoBandMatches() {
        when(repository.findMatchingBand(
                Country.ENGLAND,
                60
        )).thenReturn(Optional.empty());

        BigDecimal value = service.calculateValueInGbp(
                player(23),
                attributes(100, 120, 60, 60, 60),
                Map.of(PlayerPositionType.DEFENDER_CENTRAL, 20)
        );

        assertThat(value).isGreaterThan(BigDecimal.valueOf(2_000_000));
    }

    private BigDecimal value(
            Player player,
            PlayerAttribute attributes,
            PlayerPositionType position
    ) {
        return service.calculateValueInGbp(
                player,
                attributes,
                Map.of(position, 20)
        );
    }

    private Player player(int age) {
        return player(age, Country.ENGLAND);
    }

    private Player player(int age, Country country) {
        Club club = Club.builder()
                .country(country)
                .league(FootballLeague.EFL_CHAMPIONSHIP)
                .build();

        return Player.builder()
                .club(club)
                .dateOfBirth(LocalDate.now().minusYears(age))
                .build();
    }

    private PlayerAttribute attributes(
            int ca,
            int pa,
            int currentReputation,
            int homeReputation,
            int worldReputation
    ) {
        return PlayerAttribute.builder()
                .currentAbility(ca)
                .potentialAbility(pa)
                .currentReputation(currentReputation)
                .homeReputation(homeReputation)
                .worldReputation(worldReputation)
                .build();
    }

    private PlayerValueBand band(BigDecimal baseValue) {
        return band(Country.ENGLAND, baseValue, CurrencyCode.GBP);
    }

    private PlayerValueBand band(
            Country country,
            BigDecimal baseValue,
            CurrencyCode currency
    ) {
        return PlayerValueBand.builder()
                .country(country)
                .reputationMin(101)
                .reputationMax(150)
                .baseValue(baseValue)
                .currency(currency)
                .build();
    }
}
