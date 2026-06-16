package com.clubops.team.system;

import com.clubops.club.Club;
import com.clubops.club.Country;
import com.clubops.team.Team;
import com.clubops.team.TeamType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamSystemFactory {

    public List<Team> createDefaultTeamsForClub(Club club) {
        Country country = club.getCountry();

        return switch (country) {
            case ENGLAND -> createEnglandTeams(club);
            case SPAIN -> createSpainTeams(club);
            case ITALY -> createItalyTeams(club);
            case GERMANY -> createGermanyTeams(club);
            case FRANCE -> createFranceTeams(club);
        };
    }

    private List<Team> createEnglandTeams(Club club) {
        String clubName = club.getName();

        return List.of(
                Team.builder()
                        .club(club)
                        .name(clubName + " First Team")
                        .type(TeamType.FIRST_TEAM)
                        .displayOrder(1)
                        .build(),

                Team.builder()
                        .club(club)
                        .name(clubName + " U21")
                        .type(TeamType.U21)
                        .displayOrder(2)
                        .build(),

                Team.builder()
                        .club(club)
                        .name(clubName + " U18")
                        .type(TeamType.U18)
                        .displayOrder(3)
                        .build()
        );
    }

    private List<Team> createSpainTeams(Club club) {
        // Placeholder for future implementation.
        // Spain might use First Team, B Team, Juvenil A, etc.
        return createEnglandTeams(club);
    }

    private List<Team> createItalyTeams(Club club) {
        // Placeholder for future implementation.
        return createEnglandTeams(club);
    }

    private List<Team> createGermanyTeams(Club club) {
        // Placeholder for future implementation.
        return createEnglandTeams(club);
    }

    private List<Team> createFranceTeams(Club club) {
        // Placeholder for future implementation.
        return createEnglandTeams(club);
    }
}