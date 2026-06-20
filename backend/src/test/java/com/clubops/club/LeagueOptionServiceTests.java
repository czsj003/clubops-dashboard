package com.clubops.club;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeagueOptionServiceTests {

    private final LeagueOptionService service = new LeagueOptionService();

    @Test
    void acceptsLeagueAndRequiredGroupForCountry() {
        assertDoesNotThrow(() -> service.validateLeagueSelection(
                Country.SPAIN,
                FootballLeague.SEGUNDA_FEDERACION,
                "GROUP_3"
        ));
    }

    @Test
    void rejectsLeagueFromAnotherCountry() {
        assertThrows(IllegalArgumentException.class, () ->
                service.validateLeagueSelection(
                        Country.ENGLAND,
                        FootballLeague.LA_LIGA,
                        null
                )
        );
    }

    @Test
    void requiresGroupOnlyForGroupedLeagues() {
        assertThrows(IllegalArgumentException.class, () ->
                service.validateLeagueSelection(
                        Country.GERMANY,
                        FootballLeague.REGIONALLIGA,
                        null
                )
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.validateLeagueSelection(
                        Country.USA,
                        FootballLeague.MLS,
                        "EAST"
                )
        );
    }
}
