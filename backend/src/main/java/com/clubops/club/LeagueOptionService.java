package com.clubops.club;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LeagueOptionService {

    private static final Map<Country, List<FootballLeague>> LEAGUES_BY_COUNTRY = Map.ofEntries(
            Map.entry(Country.ENGLAND, List.of(
                    FootballLeague.PREMIER_LEAGUE,
                    FootballLeague.EFL_CHAMPIONSHIP,
                    FootballLeague.EFL_LEAGUE_ONE,
                    FootballLeague.EFL_LEAGUE_TWO,
                    FootballLeague.NATIONAL_LEAGUE,
                    FootballLeague.NATIONAL_LEAGUE_NORTH_SOUTH
            )),
            Map.entry(Country.SPAIN, List.of(
                    FootballLeague.LA_LIGA,
                    FootballLeague.LA_LIGA_2,
                    FootballLeague.PRIMERA_FEDERACION,
                    FootballLeague.SEGUNDA_FEDERACION,
                    FootballLeague.TERCERA_FEDERACION
            )),
            Map.entry(Country.ITALY, List.of(
                    FootballLeague.SERIE_A,
                    FootballLeague.SERIE_B,
                    FootballLeague.SERIE_C,
                    FootballLeague.SERIE_D
            )),
            Map.entry(Country.GERMANY, List.of(
                    FootballLeague.BUNDESLIGA,
                    FootballLeague.BUNDESLIGA_2,
                    FootballLeague.LIGA_3,
                    FootballLeague.REGIONALLIGA
            )),
            Map.entry(Country.FRANCE, List.of(
                    FootballLeague.LIGUE_1,
                    FootballLeague.LIGUE_2,
                    FootballLeague.CHAMPIONNAT_NATIONAL,
                    FootballLeague.FRANCE_REGIONAL
            )),
            Map.entry(Country.PORTUGAL, List.of(
                    FootballLeague.PRIMEIRA_LIGA,
                    FootballLeague.LIGA_PORTUGAL_2,
                    FootballLeague.PORTUGAL_LIGA_3,
                    FootballLeague.PORTUGAL_CAMPEONATO_NACIONAL
            )),
            Map.entry(Country.NETHERLANDS, List.of(
                    FootballLeague.EREDIVISIE,
                    FootballLeague.EERSTE_DIVISIE
            )),
            Map.entry(Country.BELGIUM, List.of(
                    FootballLeague.BELGIAN_PRO_LEAGUE,
                    FootballLeague.CHALLENGER_PRO_LEAGUE,
                    FootballLeague.BELGIAN_AMATEUR_FIRST_DIVISION
            )),
            Map.entry(Country.TURKEY, List.of(
                    FootballLeague.SUPER_LIG,
                    FootballLeague.TFF_1_LIG,
                    FootballLeague.TFF_2_LIG
            )),
            Map.entry(Country.SAUDI_ARABIA, List.of(
                    FootballLeague.SAUDI_PRO_LEAGUE,
                    FootballLeague.SAUDI_FIRST_DIVISION
            )),
            Map.entry(Country.CHINA, List.of(
                    FootballLeague.CHINESE_SUPER_LEAGUE,
                    FootballLeague.CHINA_LEAGUE_ONE
            )),
            Map.entry(Country.USA, List.of(FootballLeague.MLS)),
            Map.entry(Country.BRAZIL, List.of(
                    FootballLeague.BRASILEIRAO_SERIE_A,
                    FootballLeague.BRASILEIRAO_SERIE_B
            )),
            Map.entry(Country.ARGENTINA, List.of(
                    FootballLeague.ARGENTINA_PRIMERA_DIVISION,
                    FootballLeague.ARGENTINA_PRIMERA_NACIONAL
            ))
    );

    public List<FootballLeague> getLeaguesForCountry(Country country) {
        return LEAGUES_BY_COUNTRY.getOrDefault(country, List.of());
    }

    public void validateLeagueSelection(
            Country country,
            FootballLeague league,
            String leagueGroup
    ) {
        if (country == null || league == null
                || !getLeaguesForCountry(country).contains(league)) {
            throw new IllegalArgumentException("League does not belong to selected country");
        }

        List<String> groups = getGroupsForLeague(league);

        if (groups.isEmpty()) {
            if (leagueGroup != null && !leagueGroup.isBlank()) {
                throw new IllegalArgumentException("This league does not use groups");
            }
            return;
        }

        if (leagueGroup == null || leagueGroup.isBlank()) {
            throw new IllegalArgumentException("This league requires a group");
        }

        if (!groups.contains(leagueGroup)) {
            throw new IllegalArgumentException("Invalid league group");
        }
    }

    public List<String> getGroupsForLeague(FootballLeague league) {
        return switch (league) {
            case NATIONAL_LEAGUE_NORTH_SOUTH -> List.of("NORTH", "SOUTH");
            case PRIMERA_FEDERACION -> List.of("GROUP_A", "GROUP_B");
            case SEGUNDA_FEDERACION -> numberedGroups(5);
            case TERCERA_FEDERACION -> numberedGroups(18);
            case SERIE_C -> numberedGroups(3);
            case SERIE_D -> numberedGroups(9);
            case REGIONALLIGA -> List.of("WEST", "BAVARIA", "NORTHEAST", "NORTH", "SOUTHWEST");
            case FRANCE_REGIONAL -> numberedGroups(3);
            case BELGIAN_AMATEUR_FIRST_DIVISION -> List.of("FRENCH_SPEAKING", "FLEMISH");
            case TFF_2_LIG -> List.of("RED", "WHITE");
            default -> List.of();
        };
    }

    private List<String> numberedGroups(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(number -> "GROUP_" + number)
                .toList();
    }
}
