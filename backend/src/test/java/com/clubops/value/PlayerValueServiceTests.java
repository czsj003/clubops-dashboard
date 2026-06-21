package com.clubops.value;

import com.clubops.club.Club;
import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import com.clubops.currency.CurrencyCode;
import com.clubops.currency.CurrencyService;
import com.clubops.player.Player;
import com.clubops.player.PlayerAttribute;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerValueServiceTests {

    private final PlayerValueBandRepository repository =
            mock(PlayerValueBandRepository.class);
    private final CurrencyService currencyService = new CurrencyService();
    private final PlayerValueService service =
            new PlayerValueService(repository, currencyService);

    @Test
    void usesFallbackWhenNoBandMatches() {
        Player player = player();
        PlayerAttribute attributes = attributes(100, 120, 60);

        when(repository.findMatchingBand(
                Country.ENGLAND,
                FootballLeague.EFL_CHAMPIONSHIP,
                60
        )).thenReturn(Optional.empty());

        assertThat(service.calculateValueInGbp(player, attributes))
                .isEqualByComparingTo("6500000");
    }

    @Test
    void appliesBandAndPlayerMultipliers() {
        Player player = player();
        PlayerAttribute attributes = attributes(100, 120, 60);
        PlayerValueBand band = PlayerValueBand.builder()
                .country(Country.ENGLAND)
                .league(FootballLeague.EFL_CHAMPIONSHIP)
                .reputationMin(51)
                .reputationMax(75)
                .baseValue(BigDecimal.valueOf(100_000))
                .currency(CurrencyCode.GBP)
                .build();

        when(repository.findMatchingBand(
                Country.ENGLAND,
                FootballLeague.EFL_CHAMPIONSHIP,
                60
        )).thenReturn(Optional.of(band));

        assertThat(service.calculateValueInGbp(player, attributes))
                .isEqualByComparingTo("120960");
    }

    private Player player() {
        Club club = Club.builder()
                .country(Country.ENGLAND)
                .league(FootballLeague.EFL_CHAMPIONSHIP)
                .build();

        return Player.builder()
                .club(club)
                .dateOfBirth(LocalDate.now().minusYears(23))
                .build();
    }

    private PlayerAttribute attributes(int ca, int pa, int reputation) {
        return PlayerAttribute.builder()
                .currentAbility(ca)
                .potentialAbility(pa)
                .worldReputation(reputation)
                .build();
    }
}
