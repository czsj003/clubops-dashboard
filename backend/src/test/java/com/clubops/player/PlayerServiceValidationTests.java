package com.clubops.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.clubops.club.ClubRepository;
import com.clubops.contract.ContractBonusRepository;
import com.clubops.contract.PlayerContractRepository;
import com.clubops.contract.PlayerContractType;
import com.clubops.contract.WageDisplayPeriod;
import com.clubops.currency.CurrencyCode;
import com.clubops.currency.CurrencyService;
import com.clubops.player.dto.PlayerCreateRequest;
import com.clubops.team.TeamRepository;
import com.clubops.team.Team;
import com.clubops.user.User;
import com.clubops.user.UserAccountType;
import com.clubops.value.PlayerValueService;

@ExtendWith(MockitoExtension.class)
class PlayerServiceValidationTests {

    private static final List<String> GOALKEEPING_FIELDS = List.of(
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

    @Mock
    private ClubRepository clubRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerAttributeRepository playerAttributeRepository;
    @Mock
    private PlayerPositionRepository playerPositionRepository;
    @Mock
    private PlayerLanguageRepository playerLanguageRepository;
    @Mock
    private PlayerSecondaryNationalityRepository secondaryNationalityRepository;
    @Mock
    private PlayerPersonalityCalculator personalityCalculator;
    @Mock
    private MediaHandlingStyleCalculator mediaHandlingStyleCalculator;
    @Mock
    private PlayerContractRepository playerContractRepository;
    @Mock
    private ContractBonusRepository contractBonusRepository;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private PlayerAbilityResolver playerAbilityResolver;
    @Mock
    private PlayerValueService playerValueService;
    @Mock
    private NationalityLanguageService nationalityLanguageService;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void outfieldPlayersAutomaticallyReceiveZeroGoalkeepingAttributes() {
        Map<String, Integer> attributes = baseAttributes();
        PlayerCreateRequest request = request(
                Map.of(PlayerPositionType.STRIKER, 20),
                attributes
        );

        validate(request);

        GOALKEEPING_FIELDS.forEach(
                field -> assertThat(attributes.get(field)).isZero()
        );
    }

    @Test
    void outfieldPlayersStillRejectZeroNormalAttributes() {
        Map<String, Integer> attributes = baseAttributes();
        attributes.put("pace", 0);

        PlayerCreateRequest request = request(
                Map.of(PlayerPositionType.STRIKER, 20),
                attributes
        );

        assertThatThrownBy(() -> validate(request))
                .hasMessage(
                        "pace must be between 1 and 20 in developer mode"
                );
    }

    @Test
    void goalkeepersRejectZeroGoalkeepingAttributes() {
        Map<String, Integer> attributes = goalkeeperAttributes();
        attributes.put("handling", 0);

        PlayerCreateRequest request = request(
                Map.of(PlayerPositionType.GOALKEEPER, 20),
                attributes
        );

        assertThatThrownBy(() -> validate(request))
                .hasMessage(
                        "handling must be between 1 and 20 in developer mode"
                );
    }

    @Test
    void goalkeepersAllowOnlyRatingOneForStoredOutfieldPositions() {
        Map<String, Integer> attributes = goalkeeperAttributes();
        Map<PlayerPositionType, Integer> positions = Map.of(
                PlayerPositionType.GOALKEEPER, 20,
                PlayerPositionType.DEFENDER_CENTRAL, 2
        );

        assertThatThrownBy(() -> validate(request(positions, attributes)))
                .hasMessage(
                        "Goalkeepers cannot have outfield positions above 1"
                );
    }

    @Test
    void outfieldPlayersCanHaveMultipleNaturalPositions() {
        Map<PlayerPositionType, Integer> positions = Map.of(
                PlayerPositionType.ATTACKING_MIDFIELDER_LEFT, 20,
                PlayerPositionType.ATTACKING_MIDFIELDER_RIGHT, 20,
                PlayerPositionType.STRIKER, 15,
                PlayerPositionType.GOALKEEPER, 1
        );

        validate(request(positions, baseAttributes()));
    }

    @Test
    void outfieldPlayersRequireAtLeastOneNaturalOutfieldPosition() {
        Map<PlayerPositionType, Integer> positions = Map.of(
                PlayerPositionType.STRIKER, 19,
                PlayerPositionType.GOALKEEPER, 1
        );

        assertThatThrownBy(() -> validate(request(positions, baseAttributes())))
                .hasMessage(
                        "Outfield players must have at least one outfield position rated 20"
                );
    }

    @Test
    void validGoalkeeperAttributesAndPositionsPassValidation() {
        Map<PlayerPositionType, Integer> positions = Map.of(
                PlayerPositionType.GOALKEEPER, 20,
                PlayerPositionType.DEFENDER_CENTRAL, 1
        );

        validate(request(positions, goalkeeperAttributes()));
    }

    @Test
    void creationZeroAttributesResolveWhileOutfieldGoalkeepingStaysZero() {
        Map<String, Integer> attributes = baseAttributes();
        attributes.put("pace", 0);
        attributes.put("handling", 0);

        ReflectionTestUtils.invokeMethod(
                playerService,
                "normalizeRandomCreationAttributes",
                attributes,
                false
        );

        assertThat(attributes.get("pace")).isBetween(1, 20);
        assertThat(attributes.get("handling")).isZero();
    }

    @Test
    void normalUsersCannotUseVipActions() {
        User normalUser = User.builder()
                .accountType(UserAccountType.NORMAL)
                .build();

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                playerService,
                "requireVip",
                normalUser
        )).hasMessage("VIP account is required for this action");
    }

    @Test
    void vipUsersCanUseVipActions() {
        User vipUser = User.builder()
                .accountType(UserAccountType.VIP)
                .build();

        ReflectionTestUtils.invokeMethod(playerService, "requireVip", vipUser);
    }

    @Test
    void duplicateSquadNumberInSameTeamIsRejected() {
        Team team = Team.builder().id(1L).build();

        org.mockito.Mockito.when(
                playerContractRepository.existsByTeamAndSquadNumber(team, 10)
        ).thenReturn(true);

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                playerService,
                "validateSquadNumberForCreate",
                team,
                10
        )).hasMessage("Squad number is already used in this team");
    }

    @Test
    void emptySquadNumberIsAllowed() {
        Team team = Team.builder().id(1L).build();

        ReflectionTestUtils.invokeMethod(
                playerService,
                "validateSquadNumberForCreate",
                team,
                null
        );
    }

    @Test
    void nativeLanguageIsAlwaysSavedAtFluencyTen() {
        Player player = Player.builder().id(1L).build();
        when(nationalityLanguageService.getDefaultLanguage(CountryCode.CHINA))
                .thenReturn(LanguageCode.CHINESE);

        ReflectionTestUtils.invokeMethod(
                playerService,
                "savePlayerLanguages",
                player,
                CountryCode.CHINA,
                Map.of(
                        LanguageCode.CHINESE, 3,
                        LanguageCode.ENGLISH, 7
                )
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<PlayerLanguage>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(playerLanguageRepository).saveAll(captor.capture());

        assertThat(captor.getValue())
                .extracting(
                        PlayerLanguage::getLanguageCode,
                        PlayerLanguage::getFluency
                )
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple(LanguageCode.CHINESE, 10),
                        org.assertj.core.groups.Tuple.tuple(LanguageCode.ENGLISH, 7)
                );
    }

    private void validate(PlayerCreateRequest request) {
        ReflectionTestUtils.invokeMethod(
                playerService,
                "validatePlayerRequest",
                request,
                false
        );
    }

    private Map<String, Integer> baseAttributes() {
        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("currentAbility", 100);
        attributes.put("potentialAbility", 140);
        attributes.put("pace", 10);
        return attributes;
    }

    private Map<String, Integer> goalkeeperAttributes() {
        Map<String, Integer> attributes = baseAttributes();
        GOALKEEPING_FIELDS.forEach(field -> attributes.put(field, 10));
        return attributes;
    }

    private PlayerCreateRequest request(
            Map<PlayerPositionType, Integer> positions,
            Map<String, Integer> attributes
    ) {
        return new PlayerCreateRequest(
                1L,
                "Test",
                "Player",
                null,
                "Test Player",
                Race.UNKNOWN,
                HairColor.BLACK,
                HairLength.SHORT,
                SkinTone.FLESH,
                180,
                75,
                LocalDate.of(2000, 1, 1),
                "London",
                CountryCode.ENGLAND,
                CountryCode.ENGLAND,
                List.of(),
                Map.of(LanguageCode.ENGLISH, 10),
                positions,
                attributes,
                "FIXED",
                null,
                BigDecimal.valueOf(1_000_000),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2029, 6, 30),
                PlayerContractType.FULL_TIME,
                BigDecimal.valueOf(1_000),
                CurrencyCode.GBP,
                WageDisplayPeriod.WEEKLY,
                30,
                null,
                null
        );
    }
}
