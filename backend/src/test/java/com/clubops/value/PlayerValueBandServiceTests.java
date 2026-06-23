package com.clubops.value;

import com.clubops.club.Country;
import com.clubops.currency.CurrencyCode;
import com.clubops.value.dto.PlayerValueBandRequest;
import com.clubops.value.dto.PlayerValueBandBulkRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Test
    void bulkReplaceAcceptsBlankValuesAsZeroAndNonThousandValues() {
        List<PlayerValueBandBulkRequest.PlayerValueBandBulkItem> items =
                java.util.stream.IntStream.range(0, 20)
                        .mapToObj(index ->
                                new PlayerValueBandBulkRequest.PlayerValueBandBulkItem(
                                        index * 10 + 1,
                                        (index + 1) * 10,
                                        index == 0 ? BigDecimal.valueOf(2500) : null
                                ))
                        .toList();
        PlayerValueBandBulkRequest request = new PlayerValueBandBulkRequest(
                Country.ENGLAND,
                CurrencyCode.GBP,
                items
        );

        when(repository.findByCountryOrderByReputationMinAsc(Country.ENGLAND))
                .thenReturn(List.of());
        when(repository.saveAll(anyList())).thenAnswer(invocation ->
                invocation.getArgument(0));

        var responses = service.replaceBands(request);

        assertThat(responses).hasSize(20);
        assertThat(responses.get(0).baseValue()).isEqualByComparingTo("2500");
        assertThat(responses.get(1).baseValue()).isEqualByComparingTo("0");
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
