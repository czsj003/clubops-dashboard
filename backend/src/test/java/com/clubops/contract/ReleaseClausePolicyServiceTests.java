package com.clubops.contract;

import com.clubops.club.Country;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReleaseClausePolicyServiceTests {

    private final ReleaseClausePolicyService service =
            new ReleaseClausePolicyService();

    @Test
    void resolvesCountryRules() {
        assertEquals(ReleaseClauseRule.REQUIRED, service.getRule(Country.SPAIN));
        assertEquals(ReleaseClauseRule.REQUIRED, service.getRule(Country.PORTUGAL));
        assertEquals(ReleaseClauseRule.REQUIRED, service.getRule(Country.BRAZIL));
        assertEquals(ReleaseClauseRule.REQUIRED, service.getRule(Country.ARGENTINA));
        assertEquals(ReleaseClauseRule.FORBIDDEN, service.getRule(Country.FRANCE));
        assertEquals(ReleaseClauseRule.OPTIONAL, service.getRule(Country.ENGLAND));
    }

    @Test
    void enforcesRequiredAndForbiddenAmounts() {
        assertThrows(IllegalArgumentException.class,
                () -> service.validate(Country.SPAIN, null));
        assertDoesNotThrow(() ->
                service.validate(Country.SPAIN, BigDecimal.valueOf(1_000_000)));

        assertThrows(IllegalArgumentException.class, () ->
                service.validate(Country.FRANCE, BigDecimal.ONE));
        assertDoesNotThrow(() -> service.validate(Country.FRANCE, null));
    }
}
