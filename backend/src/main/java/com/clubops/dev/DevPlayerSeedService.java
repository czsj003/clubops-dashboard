package com.clubops.dev;

import com.clubops.club.Club;
import com.clubops.club.ClubRepository;
import com.clubops.player.*;
import com.clubops.team.Team;
import com.clubops.team.TeamRepository;
import com.clubops.team.TeamType;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DevPlayerSeedService {

    private final ClubRepository clubRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerAttributeRepository playerAttributeRepository;
    private final PlayerPositionRepository playerPositionRepository;
    private final PlayerLanguageRepository playerLanguageRepository;
    private final PlayerSecondaryNationalityRepository secondaryNationalityRepository;

    @Transactional
    public String seedPlayersForCurrentUser(User user) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        if (playerRepository.countByClub(club) > 0) {
            return "Players already exist for this club. Seed skipped.";
        }

        Team firstTeam = teamRepository.findByClubOrderByDisplayOrderAsc(club)
                .stream()
                .filter(team -> team.getType() == TeamType.FIRST_TEAM)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("First team not found"));

        createPlayer(
                club,
                firstTeam,
                "Oliver",
                "Reed",
                null,
                "Oliver James Reed",
                LocalDate.of(2001, 4, 12),
                "Leeds",
                CountryCode.ENGLAND,
                CountryCode.ENGLAND,
                PlayerPositionType.STRIKER,
                20,
                128,
                165
        );

        createPlayer(
                club,
                firstTeam,
                "Daniel",
                "Cole",
                null,
                "Daniel Thomas Cole",
                LocalDate.of(1998, 9, 3),
                "Manchester",
                CountryCode.ENGLAND,
                CountryCode.ENGLAND,
                PlayerPositionType.MIDFIELDER_CENTRAL,
                20,
                135,
                152
        );

        createPlayer(
                club,
                firstTeam,
                "Mateo",
                "Silva",
                "Mateo",
                "Mateo Rafael Silva",
                LocalDate.of(2003, 1, 18),
                "Lisbon",
                CountryCode.PORTUGAL,
                CountryCode.PORTUGAL,
                PlayerPositionType.ATTACKING_MIDFIELDER_CENTRAL,
                20,
                118,
                170
        );

        createPlayer(
                club,
                firstTeam,
                "Ethan",
                "Brooks",
                null,
                "Ethan Michael Brooks",
                LocalDate.of(2000, 7, 21),
                "Birmingham",
                CountryCode.ENGLAND,
                CountryCode.ENGLAND,
                PlayerPositionType.DEFENDER_CENTRAL,
                20,
                122,
                148
        );

        createPlayer(
                club,
                firstTeam,
                "Noah",
                "Walker",
                null,
                "Noah George Walker",
                LocalDate.of(1999, 11, 7),
                "London",
                CountryCode.ENGLAND,
                CountryCode.ENGLAND,
                PlayerPositionType.GOALKEEPER,
                20,
                130,
                155
        );

        return "Seeded 5 players for " + club.getName();
    }

    private void createPlayer(
            Club club,
            Team team,
            String firstName,
            String lastName,
            String commonName,
            String fullName,
            LocalDate dateOfBirth,
            String birthCity,
            CountryCode birthCountry,
            CountryCode nationality,
            PlayerPositionType mainPosition,
            int mainPositionRating,
            int ca,
            int pa
    ) {
        Player player = Player.builder()
                .club(club)
                .team(team)
                .firstName(firstName)
                .lastName(lastName)
                .commonName(commonName)
                .fullName(fullName)
                .race(Race.UNKNOWN)
                .hairColor(HairColor.UNKNOWN)
                .hairLength(HairLength.UNKNOWN)
                .skinTone(SkinTone.UNKNOWN)
                .heightCm(180)
                .weightKg(75)
                .dateOfBirth(dateOfBirth)
                .birthCity(birthCity)
                .birthCountry(birthCountry)
                .nationality(nationality)
                .build();

        Player savedPlayer = playerRepository.save(player);

        PlayerAttribute attributes = createDefaultAttributes(savedPlayer, ca, pa, mainPosition);
        playerAttributeRepository.save(attributes);

        List<PlayerPosition> positions = createDefaultPositions(
                savedPlayer,
                mainPosition,
                mainPositionRating
        );

        validatePositionRules(positions);

        playerPositionRepository.saveAll(positions);

        PlayerLanguage english = PlayerLanguage.builder()
                .player(savedPlayer)
                .languageCode(LanguageCode.ENGLISH)
                .fluency(10)
                .build();

        playerLanguageRepository.save(english);

        if (nationality == CountryCode.PORTUGAL) {
            PlayerSecondaryNationality secondaryNationality = PlayerSecondaryNationality.builder()
                    .player(savedPlayer)
                    .countryCode(CountryCode.ENGLAND)
                    .build();

            secondaryNationalityRepository.save(secondaryNationality);
        }
    }

    private PlayerAttribute createDefaultAttributes(
            Player player,
            int ca,
            int pa,
            PlayerPositionType mainPosition
    ) {
        boolean isGoalkeeper = mainPosition == PlayerPositionType.GOALKEEPER;

        return PlayerAttribute.builder()
                .player(player)
                .currentAbility(ca)
                .potentialAbility(pa)
                .currentReputation(80)
                .homeReputation(80)
                .worldReputation(50)
                .leftFoot(10)
                .rightFoot(20)

                .adaptability(10)
                .ambition(12)
                .controversy(5)
                .loyalty(11)
                .pressure(12)
                .professionalism(13)
                .sportsmanship(12)
                .temperament(11)

                .aggression(10)
                .anticipation(12)
                .bravery(12)
                .composure(12)
                .concentration(11)
                .consistency(10)
                .decisions(12)
                .determination(13)
                .dirtiness(5)
                .flair(11)
                .importantMatches(10)
                .leadership(10)
                .movement(12)
                .positioning(12)
                .teamWork(13)
                .vision(12)
                .workRate(13)

                .acceleration(12)
                .agility(12)
                .balance(12)
                .injuryProneness(8)
                .jumpingReach(11)
                .naturalFitness(12)
                .pace(12)
                .stamina(13)
                .strength(12)

                .corners(10)
                .crossing(10)
                .dribbling(11)
                .finishing(11)
                .firstTouch(12)
                .freeKicks(10)
                .heading(10)
                .longShots(10)
                .longThrows(8)
                .marking(10)
                .passing(12)
                .penaltyTaking(10)
                .tackling(10)
                .technique(12)
                .versatility(10)

                .aerialAbility(isGoalkeeper ? 13 : 0)
                .commandOfArea(isGoalkeeper ? 12 : 0)
                .communication(isGoalkeeper ? 12 : 0)
                .eccentricity(isGoalkeeper ? 8 : 0)
                .handling(isGoalkeeper ? 13 : 0)
                .kicking(isGoalkeeper ? 12 : 0)
                .oneOnOnes(isGoalkeeper ? 13 : 0)
                .reflexes(isGoalkeeper ? 14 : 0)
                .rushingOut(isGoalkeeper ? 11 : 0)
                .tendencyToPunch(isGoalkeeper ? 8 : 0)
                .throwing(isGoalkeeper ? 12 : 0)
                .build();
    }

    private void validatePositionRules(List<PlayerPosition> positions) {
        boolean hasNaturalPosition = positions.stream()
                .anyMatch(position -> position.getRating() == 20);

        if (!hasNaturalPosition) {
            throw new IllegalArgumentException("Each player must have at least one position rated 20");
        }

        boolean isNaturalGoalkeeper = positions.stream()
                .anyMatch(position ->
                        position.getPositionType() == PlayerPositionType.GOALKEEPER
                                && position.getRating() == 20
                );

        if (isNaturalGoalkeeper) {
            boolean hasInvalidOutfieldPosition = positions.stream()
                    .anyMatch(position ->
                            position.getPositionType() != PlayerPositionType.GOALKEEPER
                                    && position.getRating() != 1
                    );

            if (hasInvalidOutfieldPosition) {
                throw new IllegalArgumentException(
                        "If Goalkeeper is rated 20, all other positions must be rated 1"
                );
            }
        }
    }

    private List<PlayerPosition> createDefaultPositions(
            Player player,
            PlayerPositionType mainPosition,
            int mainPositionRating
    ) {
        if (mainPositionRating != 20) {
            throw new IllegalArgumentException("Main position must be rated 20");
        }

        if (mainPosition == PlayerPositionType.GOALKEEPER) {
            return List.of(
                    PlayerPosition.builder()
                            .player(player)
                            .positionType(PlayerPositionType.GOALKEEPER)
                            .rating(20)
                            .build(),

                    PlayerPosition.builder()
                            .player(player)
                            .positionType(PlayerPositionType.DEFENDER_CENTRAL)
                            .rating(1)
                            .build(),

                    PlayerPosition.builder()
                            .player(player)
                            .positionType(PlayerPositionType.DEFENSIVE_MIDFIELDER)
                            .rating(1)
                            .build(),

                    PlayerPosition.builder()
                            .player(player)
                            .positionType(PlayerPositionType.MIDFIELDER_CENTRAL)
                            .rating(1)
                            .build(),

                    PlayerPosition.builder()
                            .player(player)
                            .positionType(PlayerPositionType.STRIKER)
                            .rating(1)
                            .build()
            );
        }

        return List.of(
                PlayerPosition.builder()
                        .player(player)
                        .positionType(mainPosition)
                        .rating(20)
                        .build()
        );
    }
}