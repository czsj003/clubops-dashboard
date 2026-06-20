package com.clubops.team.system;

import com.clubops.club.Club;
import com.clubops.club.Country;
import com.clubops.team.Team;
import com.clubops.team.TeamType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TeamSystemFactory {

    public List<Team> createDefaultTeamsForClub(Club club) {
        Country country = club.getCountry();

        return switch (country) {
            case ENGLAND -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("U21", TeamType.U21, 2),
                    spec("U18", TeamType.U18, 3));
            case SPAIN -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("B Team", TeamType.B_TEAM, 2),
                    spec("U19", TeamType.U19, 3));
            case ITALY -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("U20", TeamType.U20, 2),
                    spec("U18", TeamType.U18, 3));
            case GERMANY -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("II Team", TeamType.II_TEAM, 2),
                    spec("U19", TeamType.U19, 3));
            case FRANCE -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("Second Team", TeamType.SECOND_TEAM, 2),
                    spec("U19", TeamType.U19, 3));
            case PORTUGAL -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("B Team", TeamType.B_TEAM, 2),
                    spec("U19", TeamType.U19, 3));
            case NETHERLANDS -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("Second Team", TeamType.SECOND_TEAM, 2),
                    spec("U19", TeamType.U19, 3));
            case BELGIUM -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("B Team", TeamType.B_TEAM, 2),
                    spec("U18", TeamType.U18, 3));
            case TURKEY -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("U19", TeamType.U19, 2));
            case SAUDI_ARABIA -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("Reserve Team", TeamType.RESERVE_TEAM, 2),
                    spec("U19", TeamType.U19, 3));
            case CHINA -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("U21", TeamType.U21, 2),
                    spec("U19", TeamType.U19, 3));
            case USA -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1));
            case BRAZIL, ARGENTINA -> teams(club,
                    spec("First Team", TeamType.FIRST_TEAM, 1),
                    spec("Reserve Team", TeamType.RESERVE_TEAM, 2),
                    spec("U20", TeamType.U20, 3));
        };
    }

    private List<Team> teams(Club club, TeamSpec... specs) {
        return Arrays.stream(specs)
                .map(spec -> Team.builder()
                        .club(club)
                        .name(club.getName() + " " + spec.name())
                        .type(spec.type())
                        .displayOrder(spec.displayOrder())
                        .build())
                .toList();
    }

    private TeamSpec spec(String name, TeamType type, int displayOrder) {
        return new TeamSpec(name, type, displayOrder);
    }

    private record TeamSpec(String name, TeamType type, int displayOrder) {
    }
}
