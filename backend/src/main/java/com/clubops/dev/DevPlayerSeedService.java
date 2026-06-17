package com.clubops.dev;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubops.club.Club;
import com.clubops.club.ClubRepository;
import com.clubops.contract.ContractBonus;
import com.clubops.contract.ContractBonusRepository;
import com.clubops.contract.ContractBonusType;
import com.clubops.contract.PlayerContract;
import com.clubops.contract.PlayerContractRepository;
import com.clubops.contract.PlayerContractType;
import com.clubops.contract.WageDisplayPeriod;
import com.clubops.currency.CurrencyCode;
import com.clubops.player.CountryCode;
import com.clubops.player.HairColor;
import com.clubops.player.HairLength;
import com.clubops.player.LanguageCode;
import com.clubops.player.Player;
import com.clubops.player.PlayerAttribute;
import com.clubops.player.PlayerAttributeRepository;
import com.clubops.player.PlayerLanguage;
import com.clubops.player.PlayerLanguageRepository;
import com.clubops.player.PlayerPosition;
import com.clubops.player.PlayerPositionRepository;
import com.clubops.player.PlayerPositionType;
import com.clubops.player.PlayerRepository;
import com.clubops.player.PlayerSecondaryNationality;
import com.clubops.player.PlayerSecondaryNationalityRepository;
import com.clubops.player.Race;
import com.clubops.player.SkinTone;
import com.clubops.team.Team;
import com.clubops.team.TeamRepository;
import com.clubops.team.TeamType;
import com.clubops.user.User;

import lombok.RequiredArgsConstructor;

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
    private final PlayerContractRepository playerContractRepository;
    private final ContractBonusRepository contractBonusRepository;

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
                .estimatedValueInGbp(estimateSeedValue(ca, pa, dateOfBirth))
                .build();

        Player savedPlayer = playerRepository.save(player);

        PlayerAttribute attributes = createDefaultAttributes(
                savedPlayer,
                ca,
                pa,
                mainPosition,
                firstName,
                lastName
        );

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

        PlayerContract contract = createSeedContract(
                savedPlayer,
                club,
                team,
                ca,
                mainPosition
        );

        PlayerContract savedContract = playerContractRepository.save(contract);

        List<ContractBonus> bonuses = createSeedBonuses(savedContract, contract.getWageAmount(), mainPosition);
        contractBonusRepository.saveAll(bonuses);
    }

    private PlayerAttribute createDefaultAttributes(
            Player player,
            int ca,
            int pa,
            PlayerPositionType mainPosition,
            String firstName,
            String lastName
    ) {
        boolean isGoalkeeper = mainPosition == PlayerPositionType.GOALKEEPER;

        int professionalism = 13;
        int pressure = 12;
        int ambition = 12;
        int temperament = 11;
        int loyalty = 11;
        int sportsmanship = 12;
        int determination = 13;
        int controversy = 5;
        int leadership = 10;

        if (firstName.equals("Oliver")) {
            professionalism = 18;
            temperament = 14;
            determination = 14;
        }

        if (firstName.equals("Daniel")) {
            ambition = 14;
            determination = 18;
        }

        if (firstName.equals("Mateo")) {
            ambition = 18;
            loyalty = 3;
            determination = 12;
        }

        if (firstName.equals("Ethan")) {
            leadership = 20;
            determination = 20;
        }

        if (firstName.equals("Noah")) {
            professionalism = 15;
            pressure = 15;
            temperament = 15;
            controversy = 4;
        }

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
                .ambition(ambition)
                .controversy(controversy)
                .loyalty(loyalty)
                .pressure(pressure)
                .professionalism(professionalism)
                .sportsmanship(sportsmanship)
                .temperament(temperament)
                .aggression(10)
                .anticipation(12)
                .bravery(12)
                .composure(12)
                .concentration(11)
                .consistency(10)
                .decisions(12)
                .determination(determination)
                .dirtiness(5)
                .flair(11)
                .importantMatches(10)
                .leadership(leadership)
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
                .anyMatch(position
                        -> position.getPositionType() == PlayerPositionType.GOALKEEPER
                && position.getRating() == 20
                );

        if (isNaturalGoalkeeper) {
            boolean hasInvalidOutfieldPosition = positions.stream()
                    .anyMatch(position
                            -> position.getPositionType() != PlayerPositionType.GOALKEEPER
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

    private BigDecimal estimateSeedValue(int ca, int pa, LocalDate dateOfBirth) {
        int age = LocalDate.now().getYear() - dateOfBirth.getYear();

        long base = (long) ca * 50_000L;
        long potentialBoost = (long) Math.max(pa - ca, 0) * 75_000L;

        double ageMultiplier;

        if (age <= 21) {
            ageMultiplier = 1.4;
        } else if (age <= 25) {
            ageMultiplier = 1.2;
        } else if (age <= 29) {
            ageMultiplier = 1.0;
        } else if (age <= 32) {
            ageMultiplier = 0.7;
        } else {
            ageMultiplier = 0.4;
        }

        long value = Math.round((base + potentialBoost) * ageMultiplier);

        return BigDecimal.valueOf(value);
    }

    private PlayerContract createSeedContract(
            Player player,
            Club club,
            Team team,
            int ca,
            PlayerPositionType mainPosition
    ) {
        BigDecimal wage = estimateSeedWeeklyWage(ca);

        return PlayerContract.builder()
                .player(player)
                .club(club)
                .team(team)
                .squadNumber(generateSeedSquadNumber(mainPosition))
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2029, 6, 30))
                .contractType(PlayerContractType.FULL_TIME)
                .wageAmount(wage)
                .wageCurrency(CurrencyCode.GBP)
                .wageDisplayPeriod(WageDisplayPeriod.WEEKLY)
                .releaseClauseAmount(null)
                .releaseClauseCurrency(null)
                .build();
    }

    private BigDecimal estimateSeedWeeklyWage(int ca) {
        long wage = Math.max(500L, ca * 250L);
        return BigDecimal.valueOf(wage);
    }

    private Integer generateSeedSquadNumber(PlayerPositionType mainPosition) {
        return switch (mainPosition) {
            case GOALKEEPER ->
                1;
            case DEFENDER_CENTRAL ->
                5;
            case MIDFIELDER_CENTRAL ->
                8;
            case ATTACKING_MIDFIELDER_CENTRAL ->
                10;
            case STRIKER ->
                9;
            default ->
                null;
        };
    }

    private BigDecimal divideMoney(BigDecimal amount, int divisor) {
        return amount.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP);
    }

    private List<ContractBonus> createSeedBonuses(
            PlayerContract contract,
            BigDecimal weeklyWage,
            PlayerPositionType mainPosition
    ) {
        ContractBonus appearanceFee = ContractBonus.builder()
                .contract(contract)
                .bonusType(ContractBonusType.APPEARANCE_FEE)
                .amount(divideMoney(weeklyWage, 5))
                .currency(CurrencyCode.GBP)
                .autoGenerated(true)
                .build();

        ContractBonus unusedSubstituteFee = ContractBonus.builder()
                .contract(contract)
                .bonusType(ContractBonusType.UNUSED_SUBSTITUTE_FEE)
                .amount(divideMoney(weeklyWage, 20))
                .currency(CurrencyCode.GBP)
                .autoGenerated(true)
                .build();

        if (mainPosition == PlayerPositionType.GOALKEEPER) {
            ContractBonus cleanSheetBonus = ContractBonus.builder()
                    .contract(contract)
                    .bonusType(ContractBonusType.CLEAN_SHEET_BONUS)
                    .amount(divideMoney(weeklyWage, 4))
                    .currency(CurrencyCode.GBP)
                    .autoGenerated(false)
                    .build();

            return List.of(appearanceFee, unusedSubstituteFee, cleanSheetBonus);
        }

        ContractBonus goalBonus = ContractBonus.builder()
                .contract(contract)
                .bonusType(ContractBonusType.GOAL_BONUS)
                .amount(divideMoney(weeklyWage, 3))
                .currency(CurrencyCode.GBP)
                .autoGenerated(false)
                .build();

        ContractBonus assistBonus = ContractBonus.builder()
                .contract(contract)
                .bonusType(ContractBonusType.ASSIST_BONUS)
                .amount(divideMoney(weeklyWage, 4))
                .currency(CurrencyCode.GBP)
                .autoGenerated(false)
                .build();

        return List.of(appearanceFee, unusedSubstituteFee, goalBonus, assistBonus);
    }
}
