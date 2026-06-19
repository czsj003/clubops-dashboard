package com.clubops.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
                        "If Goalkeeper is 20, all other positions must be 1"
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

    private void validate(PlayerCreateRequest request) {
        ReflectionTestUtils.invokeMethod(
                playerService,
                "validatePlayerRequest",
                request
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
                HairColor.UNKNOWN,
                HairLength.UNKNOWN,
                SkinTone.UNKNOWN,
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
