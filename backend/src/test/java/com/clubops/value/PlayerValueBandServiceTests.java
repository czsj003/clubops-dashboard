package com.clubops.value;

import com.clubops.club.Country;
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
            new PlayerValueBandService(repository);

    @Test
    void rejectsInvalidReputationRange() {
        assertThatThrownBy(() -> service.createBand(request(20, 10)))
                .hasMessage("Reputation minimum cannot be greater than maximum");
    }

    @Test
    void rejectsOverlappingReputationRanges() {
        when(repository.existsOverlappingBand(
                Country.ENGLAND,
                10,
                20,
                null
        )).thenReturn(true);

        assertThatThrownBy(() -> service.createBand(request(10, 20)))
                .hasMessage("Reputation range overlaps an existing value band");
    }

    private PlayerValueBandRequest request(int minimum, int maximum) {
        return new PlayerValueBandRequest(
                Country.ENGLAND,
                minimum,
                maximum,
                BigDecimal.valueOf(100_000),
                CurrencyCode.GBP
        );
    }
}
