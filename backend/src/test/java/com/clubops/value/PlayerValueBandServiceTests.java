package com.clubops.value;

import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import com.clubops.club.LeagueOptionService;
import com.clubops.currency.CurrencyCode;
import com.clubops.value.dto.PlayerValueBandRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerValueBandServiceTests {

    private final PlayerValueBandRepository repository =
            mock(PlayerValueBandRepository.class);
    private final PlayerValueBandService service =
            new PlayerValueBandService(repository, new LeagueOptionService());

    @Test
    void rejectsLeagueFromAnotherCountry() {
        PlayerValueBandRequest request = request(FootballLeague.LA_LIGA, 1, 10);

        assertThatThrownBy(() -> service.createBand(request))
                .hasMessage("League does not belong to selected country");
    }

    @Test
    void rejectsOverlappingReputationRanges() {
        PlayerValueBandRequest request =
                request(FootballLeague.EFL_CHAMPIONSHIP, 10, 20);

        when(repository.existsOverlappingBand(
                Country.ENGLAND,
                FootballLeague.EFL_CHAMPIONSHIP,
                10,
                20,
                null
        )).thenReturn(true);

        assertThatThrownBy(() -> service.createBand(request))
                .hasMessage("Reputation range overlaps an existing value band");
    }

    private PlayerValueBandRequest request(
            FootballLeague league,
            int minimum,
            int maximum
    ) {
        return new PlayerValueBandRequest(
                Country.ENGLAND,
                league,
                minimum,
                maximum,
                BigDecimal.valueOf(100_000),
                CurrencyCode.GBP
        );
    }
}
