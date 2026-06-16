package com.clubops.player;

import com.clubops.club.Club;
import com.clubops.club.ClubRepository;
import com.clubops.player.dto.*;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;
    private final PlayerAttributeRepository playerAttributeRepository;
    private final PlayerPositionRepository playerPositionRepository;
    private final PlayerLanguageRepository playerLanguageRepository;
    private final PlayerSecondaryNationalityRepository secondaryNationalityRepository;
    private final PlayerPersonalityCalculator personalityCalculator;
    private final MediaHandlingStyleCalculator mediaHandlingStyleCalculator;

    public List<PlayerListItemResponse> getCurrentUserPlayers(User user) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        return playerRepository.findByClub(club)
                .stream()
                .map(player -> {
                    PlayerAttribute attributes = playerAttributeRepository.findByPlayer(player)
                            .orElseThrow(() -> new RuntimeException("Attributes not found for player"));

                    List<PlayerPosition> positions = playerPositionRepository.findByPlayer(player);
                    boolean isGoalkeeper = isGoalkeeper(positions);

                    return PlayerListItemResponse.from(
                            player,
                            attributes.getCurrentAbility(),
                            attributes.getPotentialAbility(),
                            isGoalkeeper
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

        List<PlayerSecondaryNationalityResponse> secondaryNationalityResponses =
                secondaryNationalityRepository.findByPlayer(player)
                        .stream()
                        .map(PlayerSecondaryNationalityResponse::from)
                        .toList();

        PlayerPersonality personality = personalityCalculator.calculate(attributes);
        MediaHandlingStyle mediaHandlingStyle = mediaHandlingStyleCalculator.calculate(attributes);

        return PlayerDetailResponse.from(
                player,
                attributes,
                positionResponses,
                languageResponses,
                secondaryNationalityResponses,
                isGoalkeeper,
                personality,
                mediaHandlingStyle
        );
    }

    private boolean isGoalkeeper(List<PlayerPosition> positions) {
        return positions.stream()
                .anyMatch(position ->
                        position.getPositionType() == PlayerPositionType.GOALKEEPER
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
}