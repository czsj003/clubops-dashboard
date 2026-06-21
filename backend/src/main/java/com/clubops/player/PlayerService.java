package com.clubops.player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubops.club.Club;
import com.clubops.club.ClubRepository;
import com.clubops.contract.ContractBonus;
import com.clubops.contract.ContractBonusRepository;
import com.clubops.contract.ContractBonusType;
import com.clubops.contract.PlayerContract;
import com.clubops.contract.PlayerContractRepository;
import com.clubops.contract.ReleaseClausePolicyService;
import com.clubops.contract.ReleaseClauseRule;
import com.clubops.contract.WageDisplayPeriod;
import com.clubops.contract.dto.ContractBonusResponse;
import com.clubops.contract.dto.PlayerContractResponse;
import com.clubops.currency.CurrencyCode;
import com.clubops.currency.CurrencyService;
import com.clubops.player.dto.PlayerCreateRequest;
import com.clubops.player.dto.PlayerDetailResponse;
import com.clubops.player.dto.PlayerLanguageResponse;
import com.clubops.player.dto.PlayerListItemResponse;
import com.clubops.player.dto.PlayerPositionResponse;
import com.clubops.player.dto.PlayerSecondaryNationalityResponse;
import com.clubops.player.dto.PlayerValueResponse;
import com.clubops.user.User;
import com.clubops.team.Team;
import com.clubops.team.TeamRepository;
import com.clubops.value.PlayerValueService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private static final Set<String> GOALKEEPING_ATTRIBUTE_KEYS = Set.of(
            "aerialAbility",
            "commandOfArea",
            "communication",
            "eccentricity",
            "handling",
            "kicking",
            "oneOnOnes",
            "reflexes",
            "rushingOut",
            "tendencyToPunch",
            "throwing"
    );

    private static final Set<String> ABILITY_AND_REPUTATION_KEYS = Set.of(
            "currentAbility",
            "potentialAbility",
            "currentReputation",
            "homeReputation",
            "worldReputation"
    );

    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;
    private final PlayerAttributeRepository playerAttributeRepository;
    private final PlayerPositionRepository playerPositionRepository;
    private final PlayerLanguageRepository playerLanguageRepository;
    private final PlayerSecondaryNationalityRepository secondaryNationalityRepository;
    private final PlayerPersonalityCalculator personalityCalculator;
    private final MediaHandlingStyleCalculator mediaHandlingStyleCalculator;
    private final PlayerContractRepository playerContractRepository;
    private final ContractBonusRepository contractBonusRepository;
    private final CurrencyService currencyService;
    private final TeamRepository teamRepository;
    private final PlayerAbilityResolver playerAbilityResolver;
    private final ReleaseClausePolicyService releaseClausePolicyService;
    private final PlayerValueService playerValueService;

    public List<PlayerListItemResponse> getCurrentUserPlayers(
            User user,
            String search,
            Long teamId,
            PlayerPositionType position,
            CountryCode nationality
    ) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        return playerRepository.findByClub(club)
                .stream()
                .filter(player -> {
                    if (search == null || search.isBlank()) {
                        return true;
                    }

                    String keyword = search.trim().toLowerCase(Locale.ROOT);

                    return player.getDisplayName().toLowerCase(Locale.ROOT).contains(keyword)
                            || player.getFullName().toLowerCase(Locale.ROOT).contains(keyword)
                            || player.getFirstName().toLowerCase(Locale.ROOT).contains(keyword)
                            || player.getLastName().toLowerCase(Locale.ROOT).contains(keyword);
                })
                .filter(player -> teamId == null || player.getTeam().getId().equals(teamId))
                .filter(player -> nationality == null || player.getNationality() == nationality)
                .filter(player -> {
                    if (position == null) {
                        return true;
                    }

                    return playerPositionRepository.findByPlayer(player)
                            .stream()
                            .anyMatch(p
                                    -> p.getPositionType() == position
                            && p.getRating() >= 15
                            );
                })
                .map(player -> {
                    PlayerAttribute attributes = playerAttributeRepository.findByPlayer(player)
                            .orElseThrow(() -> new RuntimeException("Attributes not found for player"));

                    List<PlayerPosition> positions = playerPositionRepository.findByPlayer(player);
                    boolean isGoalkeeper = isGoalkeeper(positions);

                    PlayerContract contract = playerContractRepository.findByPlayer(player)
                            .orElse(null);

                    return PlayerListItemResponse.from(
                            player,
                            attributes.getCurrentAbility(),
                            attributes.getPotentialAbility(),
                            isGoalkeeper,
                            contract != null
                                    ? toWeeklyWage(contract.getWageAmount(), contract.getWageDisplayPeriod())
                                    : null,
                            contract != null ? contract.getWageCurrency() : null
                    );
                })
                .sorted(Comparator.comparing(PlayerListItemResponse::displayName))
                .toList();
    }

    public PlayerDetailResponse getCurrentUserPlayerDetail(User user, Long playerId) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        Player player = playerRepository.findWithClubAndTeamById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        if (!player.getClub().getId().equals(club.getId())) {
            throw new RuntimeException("You do not have access to this player");
        }

        PlayerAttribute attributes = playerAttributeRepository.findByPlayer(player)
                .orElseThrow(() -> new RuntimeException("Attributes not found for player"));

        List<PlayerPosition> positions = playerPositionRepository.findByPlayer(player);

        validatePositionRules(positions);

        boolean isGoalkeeper = isGoalkeeper(positions);

        List<PlayerPositionResponse> positionResponses = positions.stream()
                .sorted(Comparator.comparing(PlayerPosition::getPositionType))
                .map(PlayerPositionResponse::from)
                .toList();

        List<PlayerLanguageResponse> languageResponses = playerLanguageRepository.findByPlayer(player)
                .stream()
                .map(PlayerLanguageResponse::from)
                .toList();

        List<PlayerSecondaryNationalityResponse> secondaryNationalityResponses
                = secondaryNationalityRepository.findByPlayer(player)
                        .stream()
                        .map(PlayerSecondaryNationalityResponse::from)
                        .toList();

        PlayerPersonality personality = personalityCalculator.calculate(attributes);
        MediaHandlingStyle mediaHandlingStyle = mediaHandlingStyleCalculator.calculate(attributes);

        PlayerContractResponse contractResponse = playerContractRepository.findByPlayer(player)
                .map(contract -> {
                    List<ContractBonusResponse> bonusResponses = contractBonusRepository
                            .findByContractOrderByBonusTypeAsc(contract)
                            .stream()
                            .map(ContractBonusResponse::from)
                            .toList();

                    return PlayerContractResponse.from(contract, bonusResponses);
                })
                .orElse(null);

        PlayerValueResponse valueResponse = new PlayerValueResponse(
                player.getEstimatedValueInGbp(),
                CurrencyCode.GBP,
                true
        );

        return PlayerDetailResponse.from(
                player,
                attributes,
                positionResponses,
                languageResponses,
                secondaryNationalityResponses,
                isGoalkeeper,
                personality,
                mediaHandlingStyle,
                contractResponse,
                valueResponse,
                currencyService.getDefaultCurrency(),
                currencyService.getAvailableCurrencies()
        );
    }

    private boolean isGoalkeeper(List<PlayerPosition> positions) {
        return positions.stream()
                .anyMatch(position
                        -> position.getPositionType() == PlayerPositionType.GOALKEEPER
                && position.getRating() == 20
                );
    }

    private void validatePositionRules(List<PlayerPosition> positions) {
        boolean hasNaturalPosition = positions.stream()
                .anyMatch(position -> position.getRating() == 20);

        if (!hasNaturalPosition) {
            throw new IllegalArgumentException("Each player must have at least one position rated 20");
        }

        boolean isNaturalGoalkeeper = isGoalkeeper(positions);

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

    @Transactional
    public PlayerDetailResponse createPlayer(User user, PlayerCreateRequest request) {
        validatePlayerRequest(request, true);

        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!team.getClub().getId().equals(club.getId())) {
            throw new RuntimeException("Team does not belong to current user's club");
        }

        Player player = Player.builder()
                .club(club)
                .team(team)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .commonName(request.commonName())
                .fullName(request.fullName())
                .race(request.race() != null ? request.race() : Race.UNKNOWN)
                .hairColor(request.hairColor() != null ? request.hairColor() : HairColor.UNKNOWN)
                .hairLength(request.hairLength() != null ? request.hairLength() : HairLength.UNKNOWN)
                .skinTone(request.skinTone() != null ? request.skinTone() : SkinTone.UNKNOWN)
                .heightCm(request.heightCm())
                .weightKg(request.weightKg())
                .dateOfBirth(request.dateOfBirth())
                .birthCity(request.birthCity())
                .birthCountry(request.birthCountry())
                .nationality(request.nationality())
                .build();

        Player savedPlayer = playerRepository.save(player);

        PlayerAttribute attributes = buildAttributes(savedPlayer, request.attributes());
        PlayerAttribute savedAttributes = playerAttributeRepository.save(attributes);

        savedPlayer.setEstimatedValueInGbp(
                playerValueService.calculateValueInGbp(savedPlayer, savedAttributes)
        );
        playerRepository.save(savedPlayer);

        List<PlayerPosition> positions = request.positions()
                .entrySet()
                .stream()
                .map(entry -> PlayerPosition.builder()
                .player(savedPlayer)
                .positionType(entry.getKey())
                .rating(entry.getValue())
                .build())
                .toList();

        validatePositionRules(positions);
        playerPositionRepository.saveAll(positions);

        if (request.languages() != null) {
            List<PlayerLanguage> languages = request.languages()
                    .entrySet()
                    .stream()
                    .map(entry -> PlayerLanguage.builder()
                    .player(savedPlayer)
                    .languageCode(entry.getKey())
                    .fluency(entry.getValue())
                    .build())
                    .toList();

            playerLanguageRepository.saveAll(languages);
        }

        if (request.secondaryNationalities() != null) {
            List<PlayerSecondaryNationality> secondaryNationalities
                    = request.secondaryNationalities()
                            .stream()
                            .map(country -> PlayerSecondaryNationality.builder()
                            .player(savedPlayer)
                            .countryCode(country)
                            .build())
                            .toList();

            secondaryNationalityRepository.saveAll(secondaryNationalities);
        }

        releaseClausePolicyService.validate(
                club.getCountry(),
                request.releaseClauseAmount()
        );

        BigDecimal releaseClauseAmount = request.releaseClauseAmount();
        CurrencyCode releaseClauseCurrency = request.releaseClauseCurrency();

        if (releaseClausePolicyService.getRule(club.getCountry())
                == ReleaseClauseRule.FORBIDDEN) {
            releaseClauseAmount = null;
            releaseClauseCurrency = null;
        }

        PlayerContract contract = PlayerContract.builder()
                .player(savedPlayer)
                .club(club)
                .team(team)
                .squadNumber(request.squadNumber())
                .startDate(request.contractStartDate())
                .endDate(request.contractEndDate())
                .contractType(request.contractType())
                .wageAmount(request.wageAmount())
                .wageCurrency(request.wageCurrency())
                .wageDisplayPeriod(request.wageDisplayPeriod())
                .releaseClauseAmount(releaseClauseAmount)
                .releaseClauseCurrency(releaseClauseCurrency)
                .build();

        PlayerContract savedContract = playerContractRepository.save(contract);

        BigDecimal weeklyWage = toWeeklyWage(
                request.wageAmount(),
                request.wageDisplayPeriod()
        );
        List<ContractBonus> autoBonuses = createAutoBonuses(
                savedContract,
                weeklyWage,
                request.wageCurrency()
        );
        contractBonusRepository.saveAll(autoBonuses);

        return getCurrentUserPlayerDetail(user, savedPlayer.getId());
    }

    private PlayerAttribute buildAttributes(Player player, Map<String, Integer> map) {
        int ca = value(map, "currentAbility");
        int pa = value(map, "potentialAbility");

        if (pa < ca) {
            throw new IllegalArgumentException("PA cannot be lower than CA");
        }

        return PlayerAttribute.builder()
                .player(player)
                .currentAbility(ca)
                .potentialAbility(pa)
                .currentReputation(value(map, "currentReputation"))
                .homeReputation(value(map, "homeReputation"))
                .worldReputation(value(map, "worldReputation"))
                .leftFoot(value(map, "leftFoot"))
                .rightFoot(value(map, "rightFoot"))
                .adaptability(value(map, "adaptability"))
                .ambition(value(map, "ambition"))
                .controversy(value(map, "controversy"))
                .loyalty(value(map, "loyalty"))
                .pressure(value(map, "pressure"))
                .professionalism(value(map, "professionalism"))
                .sportsmanship(value(map, "sportsmanship"))
                .temperament(value(map, "temperament"))
                .aggression(value(map, "aggression"))
                .anticipation(value(map, "anticipation"))
                .bravery(value(map, "bravery"))
                .composure(value(map, "composure"))
                .concentration(value(map, "concentration"))
                .consistency(value(map, "consistency"))
                .decisions(value(map, "decisions"))
                .determination(value(map, "determination"))
                .dirtiness(value(map, "dirtiness"))
                .flair(value(map, "flair"))
                .importantMatches(value(map, "importantMatches"))
                .leadership(value(map, "leadership"))
                .movement(value(map, "movement"))
                .positioning(value(map, "positioning"))
                .teamWork(value(map, "teamWork"))
                .vision(value(map, "vision"))
                .workRate(value(map, "workRate"))
                .acceleration(value(map, "acceleration"))
                .agility(value(map, "agility"))
                .balance(value(map, "balance"))
                .injuryProneness(value(map, "injuryProneness"))
                .jumpingReach(value(map, "jumpingReach"))
                .naturalFitness(value(map, "naturalFitness"))
                .pace(value(map, "pace"))
                .stamina(value(map, "stamina"))
                .strength(value(map, "strength"))
                .corners(value(map, "corners"))
                .crossing(value(map, "crossing"))
                .dribbling(value(map, "dribbling"))
                .finishing(value(map, "finishing"))
                .firstTouch(value(map, "firstTouch"))
                .freeKicks(value(map, "freeKicks"))
                .heading(value(map, "heading"))
                .longShots(value(map, "longShots"))
                .longThrows(value(map, "longThrows"))
                .marking(value(map, "marking"))
                .passing(value(map, "passing"))
                .penaltyTaking(value(map, "penaltyTaking"))
                .tackling(value(map, "tackling"))
                .technique(value(map, "technique"))
                .versatility(value(map, "versatility"))
                .aerialAbility(value(map, "aerialAbility"))
                .commandOfArea(value(map, "commandOfArea"))
                .communication(value(map, "communication"))
                .eccentricity(value(map, "eccentricity"))
                .handling(value(map, "handling"))
                .kicking(value(map, "kicking"))
                .oneOnOnes(value(map, "oneOnOnes"))
                .reflexes(value(map, "reflexes"))
                .rushingOut(value(map, "rushingOut"))
                .tendencyToPunch(value(map, "tendencyToPunch"))
                .throwing(value(map, "throwing"))
                .build();
    }

    private int value(Map<String, Integer> map, String key) {
        Integer value = map.get(key);

        if (value == null) {
            throw new IllegalArgumentException("Missing attribute: " + key);
        }

        return value;
    }

    private void validateDeveloperAttributes(
            Map<String, Integer> attributes,
            boolean isGoalkeeper
    ) {
        if (attributes == null || attributes.isEmpty()) {
            throw new IllegalArgumentException("Attributes are required");
        }

        for (Map.Entry<String, Integer> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            if (value == null) {
                throw new IllegalArgumentException(key + " is required");
            }

            boolean isGoalkeepingAttribute = GOALKEEPING_ATTRIBUTE_KEYS.contains(key);
            boolean isAbilityOrReputation = ABILITY_AND_REPUTATION_KEYS.contains(key);

            if (!isGoalkeeper && isGoalkeepingAttribute) {
                if (value != 0) {
                    throw new IllegalArgumentException(
                            "Non-goalkeepers must have goalkeeping attributes set to 0"
                    );
                }

                continue;
            }

            if (isAbilityOrReputation) {
                if (value < 1 || value > 200) {
                    throw new IllegalArgumentException(
                            key + " must be between 1 and 200 in developer mode"
                    );
                }
            } else {
                if (value < 1 || value > 20) {
                    throw new IllegalArgumentException(
                            key + " must be between 1 and 20 in developer mode"
                    );
                }
            }
        }

        Integer ca = attributes.get("currentAbility");
        Integer pa = attributes.get("potentialAbility");

        if (ca != null && pa != null && pa < ca) {
            throw new IllegalArgumentException("PA cannot be lower than CA");
        }
    }

    private boolean isGoalkeeperPositionMap(Map<PlayerPositionType, Integer> positions) {
        if (positions == null) {
            return false;
        }

        return positions.entrySet()
                .stream()
                .anyMatch(entry
                        -> entry.getKey() == PlayerPositionType.GOALKEEPER
                && Integer.valueOf(20).equals(entry.getValue())
                );
    }

    private void normalizeGoalkeepingAttributes(
            Map<String, Integer> attributes,
            boolean isGoalkeeper
    ) {
        if (attributes == null || isGoalkeeper) {
            return;
        }

        for (String key : GOALKEEPING_ATTRIBUTE_KEYS) {
            attributes.put(key, 0);
        }
    }

    private void validatePositionMap(Map<PlayerPositionType, Integer> positions) {
        if (positions == null || positions.isEmpty()) {
            throw new IllegalArgumentException("At least one position is required");
        }

        for (Map.Entry<PlayerPositionType, Integer> entry : positions.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Position type is required");
            }

            Integer value = entry.getValue();

            if (value == null || value < 1 || value > 20) {
                throw new IllegalArgumentException("Position rating must be between 1 and 20");
            }
        }

        boolean isGoalkeeper = isGoalkeeperPositionMap(positions);

        if (isGoalkeeper) {
            boolean invalidOutfieldPosition = positions.entrySet()
                    .stream()
                    .anyMatch(entry ->
                            entry.getKey() != PlayerPositionType.GOALKEEPER
                                    && entry.getValue() != 1
                    );

            if (invalidOutfieldPosition) {
                throw new IllegalArgumentException(
                        "Goalkeepers cannot have outfield positions above 1"
                );
            }
            return;
        }

        boolean hasNaturalOutfieldPosition = positions.entrySet()
                .stream()
                .anyMatch(entry ->
                        entry.getKey() != PlayerPositionType.GOALKEEPER
                                && entry.getValue() == 20
                );

        if (!hasNaturalOutfieldPosition) {
            throw new IllegalArgumentException(
                    "Outfield players must have at least one outfield position rated 20"
            );
        }
    }

    private List<ContractBonus> createAutoBonuses(
            PlayerContract contract,
            BigDecimal weeklyWage,
            CurrencyCode currency
    ) {
        ContractBonus appearanceFee = ContractBonus.builder()
                .contract(contract)
                .bonusType(ContractBonusType.APPEARANCE_FEE)
                .amount(weeklyWage.divide(BigDecimal.valueOf(5), 2, RoundingMode.HALF_UP))
                .currency(currency)
                .autoGenerated(true)
                .build();

        ContractBonus unusedSubstituteFee = ContractBonus.builder()
                .contract(contract)
                .bonusType(ContractBonusType.UNUSED_SUBSTITUTE_FEE)
                .amount(weeklyWage.divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP))
                .currency(currency)
                .autoGenerated(true)
                .build();

        return List.of(appearanceFee, unusedSubstituteFee);
    }

    private BigDecimal toWeeklyWage(BigDecimal wageAmount, WageDisplayPeriod displayPeriod) {
        return switch (displayPeriod) {
            case WEEKLY -> wageAmount;
            case MONTHLY -> wageAmount
                    .multiply(BigDecimal.valueOf(12))
                    .divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
            case YEARLY -> wageAmount.divide(BigDecimal.valueOf(52), 2, RoundingMode.HALF_UP);
        };
    }

    @Transactional
    public PlayerDetailResponse updatePlayer(User user, Long playerId, PlayerCreateRequest request) {
        validatePlayerRequest(request, false);

        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        Player player = playerRepository.findWithClubAndTeamById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        if (!player.getClub().getId().equals(club.getId())) {
            throw new RuntimeException("You do not have access to this player");
        }

        boolean existingPlayerIsGoalkeeper = isGoalkeeper(
                playerPositionRepository.findByPlayer(player)
        );
        boolean requestedPlayerIsGoalkeeper = isGoalkeeperPositionMap(request.positions());

        if (existingPlayerIsGoalkeeper && !requestedPlayerIsGoalkeeper) {
            throw new IllegalArgumentException(
                    "Goalkeeper players must keep Goalkeeper rated 20"
            );
        }

        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!team.getClub().getId().equals(club.getId())) {
            throw new RuntimeException("Team does not belong to current user's club");
        }

        player.setTeam(team);
        player.setFirstName(request.firstName());
        player.setLastName(request.lastName());
        player.setCommonName(request.commonName());
        player.setFullName(request.fullName());
        player.setRace(request.race() != null ? request.race() : Race.UNKNOWN);
        player.setHairColor(request.hairColor() != null ? request.hairColor() : HairColor.UNKNOWN);
        player.setHairLength(request.hairLength() != null ? request.hairLength() : HairLength.UNKNOWN);
        player.setSkinTone(request.skinTone() != null ? request.skinTone() : SkinTone.UNKNOWN);
        player.setHeightCm(request.heightCm());
        player.setWeightKg(request.weightKg());
        player.setDateOfBirth(request.dateOfBirth());
        player.setBirthCity(request.birthCity());
        player.setBirthCountry(request.birthCountry());
        player.setNationality(request.nationality());
        playerRepository.save(player);

        PlayerAttribute oldAttributes = playerAttributeRepository.findByPlayer(player)
                .orElseThrow(() -> new RuntimeException("Attributes not found"));

        playerAttributeRepository.delete(oldAttributes);
        playerAttributeRepository.flush();
        PlayerAttribute updatedAttributes = playerAttributeRepository.save(
                buildAttributes(player, request.attributes())
        );

        player.setEstimatedValueInGbp(
                playerValueService.calculateValueInGbp(player, updatedAttributes)
        );
        playerRepository.save(player);

        playerPositionRepository.deleteAll(playerPositionRepository.findByPlayer(player));
        playerPositionRepository.flush();

        List<PlayerPosition> newPositions = request.positions()
                .entrySet()
                .stream()
                .map(entry -> PlayerPosition.builder()
                .player(player)
                .positionType(entry.getKey())
                .rating(entry.getValue())
                .build())
                .toList();

        validatePositionRules(newPositions);
        playerPositionRepository.saveAll(newPositions);

        playerLanguageRepository.deleteAll(playerLanguageRepository.findByPlayer(player));
        playerLanguageRepository.flush();

        if (request.languages() != null) {
            playerLanguageRepository.saveAll(
                    request.languages()
                            .entrySet()
                            .stream()
                            .map(entry -> PlayerLanguage.builder()
                            .player(player)
                            .languageCode(entry.getKey())
                            .fluency(entry.getValue())
                            .build())
                            .toList()
            );
        }

        secondaryNationalityRepository.deleteAll(secondaryNationalityRepository.findByPlayer(player));
        secondaryNationalityRepository.flush();

        if (request.secondaryNationalities() != null) {
            secondaryNationalityRepository.saveAll(
                    request.secondaryNationalities()
                            .stream()
                            .map(country -> PlayerSecondaryNationality.builder()
                            .player(player)
                            .countryCode(country)
                            .build())
                            .toList()
            );
        }

        PlayerContract contract = playerContractRepository.findByPlayer(player)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        releaseClausePolicyService.validate(
                club.getCountry(),
                request.releaseClauseAmount()
        );

        BigDecimal releaseClauseAmount = request.releaseClauseAmount();
        CurrencyCode releaseClauseCurrency = request.releaseClauseCurrency();

        if (releaseClausePolicyService.getRule(club.getCountry())
                == ReleaseClauseRule.FORBIDDEN) {
            releaseClauseAmount = null;
            releaseClauseCurrency = null;
        }

        contract.setTeam(team);
        contract.setSquadNumber(request.squadNumber());
        contract.setStartDate(request.contractStartDate());
        contract.setEndDate(request.contractEndDate());
        contract.setContractType(request.contractType());
        contract.setWageAmount(request.wageAmount());
        contract.setWageCurrency(request.wageCurrency());
        contract.setWageDisplayPeriod(request.wageDisplayPeriod());
        contract.setReleaseClauseAmount(releaseClauseAmount);
        contract.setReleaseClauseCurrency(releaseClauseCurrency);

        playerContractRepository.save(contract);

        List<ContractBonus> autoGeneratedBonuses = contractBonusRepository
                .findByContractOrderByBonusTypeAsc(contract)
                .stream()
                .filter(bonus -> Boolean.TRUE.equals(bonus.getAutoGenerated()))
                .toList();

        contractBonusRepository.deleteAll(autoGeneratedBonuses);
        contractBonusRepository.flush();

        BigDecimal weeklyWage = toWeeklyWage(
                request.wageAmount(),
                request.wageDisplayPeriod()
        );
        contractBonusRepository.saveAll(
                createAutoBonuses(contract, weeklyWage, request.wageCurrency())
        );

        return getCurrentUserPlayerDetail(user, player.getId());
    }

    private void validatePlayerRequest(
            PlayerCreateRequest request,
            boolean isCreation
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Player request is required");
        }
        if (request.teamId() == null) {
            throw new IllegalArgumentException("Team is required");
        }
        requireText(request.firstName(), "First name");
        requireText(request.lastName(), "Last name");

        if (request.heightCm() == null || request.heightCm() <= 0) {
            throw new IllegalArgumentException("Height must be greater than 0");
        }
        if (request.weightKg() == null || request.weightKg() <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        if (request.dateOfBirth() == null || request.dateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth must not be in the future");
        }
        requireText(request.birthCity(), "Birth city");
        if (request.birthCountry() == null) {
            throw new IllegalArgumentException("Birth country is required");
        }
        if (request.nationality() == null) {
            throw new IllegalArgumentException("Nationality is required");
        }
        validatePositionMap(request.positions());
        boolean isGoalkeeper = isGoalkeeperPositionMap(request.positions());

        if (isCreation) {
            normalizeRandomCreationAttributes(request.attributes(), isGoalkeeper);
            playerAbilityResolver.resolve(
                    request.dateOfBirth(),
                    request.attributes(),
                    request.potentialMode(),
                    request.negativePotentialLevel()
            );
            normalizeGoalkeepingAttributes(request.attributes(), isGoalkeeper);
            validateCreationAttributes(request.attributes(), isGoalkeeper);
        } else {
            normalizeGoalkeepingAttributes(request.attributes(), isGoalkeeper);
            validateDeveloperAttributes(request.attributes(), isGoalkeeper);
        }

        validateLanguages(request.languages());
        validateSecondaryNationalities(request);
        validateContract(request);
    }

    private void normalizeRandomCreationAttributes(
            Map<String, Integer> attributes,
            boolean isGoalkeeper
    ) {
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes are required");
        }

        for (Map.Entry<String, Integer> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            if (value == null) {
                throw new IllegalArgumentException(key + " is required");
            }

            if (!isGoalkeeper && GOALKEEPING_ATTRIBUTE_KEYS.contains(key)) {
                entry.setValue(0);
                continue;
            }

            if (key.equals("currentAbility") || key.equals("potentialAbility")) {
                continue;
            }

            if (value == 0) {
                if (ABILITY_AND_REPUTATION_KEYS.contains(key)) {
                    entry.setValue(random(1, 200));
                } else {
                    entry.setValue(random(1, 20));
                }
            }
        }
    }

    private void validateCreationAttributes(
            Map<String, Integer> attributes,
            boolean isGoalkeeper
    ) {
        validateDeveloperAttributes(attributes, isGoalkeeper);
    }

    private int random(int min, int max) {
        return java.util.concurrent.ThreadLocalRandom.current()
                .nextInt(min, max + 1);
    }

    private void validateLanguages(Map<LanguageCode, Integer> languages) {
        if (languages == null) {
            return;
        }

        languages.forEach((language, fluency) -> {
            if (language == null) {
                throw new IllegalArgumentException("Language is required");
            }
            if (fluency == null || fluency < 1 || fluency > 10) {
                throw new IllegalArgumentException("Language fluency must be between 1 and 10");
            }
        });
    }

    private void validateSecondaryNationalities(PlayerCreateRequest request) {
        if (request.secondaryNationalities() == null) {
            return;
        }
        if (request.secondaryNationalities().stream().anyMatch(country -> country == null)) {
            throw new IllegalArgumentException("Secondary nationality cannot be null");
        }
        if (request.secondaryNationalities().stream().distinct().count()
                != request.secondaryNationalities().size()) {
            throw new IllegalArgumentException("Secondary nationalities must be unique");
        }
        if (request.secondaryNationalities().contains(request.nationality())) {
            throw new IllegalArgumentException(
                    "Primary nationality cannot also be a secondary nationality"
            );
        }
    }

    private void validateContract(PlayerCreateRequest request) {
        if (request.contractStartDate() == null || request.contractEndDate() == null) {
            throw new IllegalArgumentException("Contract start and end dates are required");
        }
        if (request.contractEndDate().isBefore(request.contractStartDate())) {
            throw new IllegalArgumentException("Contract end date cannot be before start date");
        }
        if (request.contractType() == null) {
            throw new IllegalArgumentException("Contract type is required");
        }
        if (request.wageAmount() == null
                || request.wageAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Wage amount cannot be negative");
        }
        if (request.wageCurrency() == null) {
            throw new IllegalArgumentException("Wage currency is required");
        }
        if (request.wageDisplayPeriod() == null) {
            throw new IllegalArgumentException("Wage display period is required");
        }
        if (request.squadNumber() != null
                && (request.squadNumber() < 1 || request.squadNumber() > 99)) {
            throw new IllegalArgumentException("Squad number must be between 1 and 99");
        }
        if (request.releaseClauseAmount() != null
                && request.releaseClauseAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Release clause amount cannot be negative");
        }
        if (request.releaseClauseAmount() != null && request.releaseClauseCurrency() == null) {
            throw new IllegalArgumentException(
                    "Release clause currency is required when an amount is provided"
            );
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
    @Transactional
    public void deletePlayer(User user, Long playerId) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        Player player = playerRepository.findWithClubAndTeamById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        if (!player.getClub().getId().equals(club.getId())) {
            throw new RuntimeException("You do not have access to this player");
        }

        PlayerContract contract = playerContractRepository.findByPlayer(player)
                .orElse(null);

        if (contract != null) {
            contractBonusRepository.deleteAll(
                    contractBonusRepository.findByContractOrderByBonusTypeAsc(contract)
            );
            playerContractRepository.delete(contract);
        }

        playerLanguageRepository.deleteAll(playerLanguageRepository.findByPlayer(player));
        secondaryNationalityRepository.deleteAll(secondaryNationalityRepository.findByPlayer(player));
        playerPositionRepository.deleteAll(playerPositionRepository.findByPlayer(player));

        playerAttributeRepository.findByPlayer(player)
                .ifPresent(playerAttributeRepository::delete);

        playerRepository.delete(player);
    }
}
