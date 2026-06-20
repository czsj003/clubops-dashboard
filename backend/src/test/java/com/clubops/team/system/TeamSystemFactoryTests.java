package com.clubops.team.system;

import com.clubops.club.Club;
import com.clubops.club.Country;
import com.clubops.team.Team;
import com.clubops.team.TeamType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamSystemFactoryTests {

    private final TeamSystemFactory factory = new TeamSystemFactory();

    @Test
    void createsSpainTeamStructure() {
        List<Team> teams = factory.createDefaultTeamsForClub(
                Club.builder().name("Madrid Test").country(Country.SPAIN).build()
        );

        assertEquals(
                List.of(TeamType.FIRST_TEAM, TeamType.B_TEAM, TeamType.U19),
                teams.stream().map(Team::getType).toList()
        );
    }

    @Test
    void createsOnlyFirstTeamForUsa() {
        List<Team> teams = factory.createDefaultTeamsForClub(
                Club.builder().name("Austin Test").country(Country.USA).build()
        );

        assertEquals(1, teams.size());
        assertEquals(TeamType.FIRST_TEAM, teams.get(0).getType());
    }
}
