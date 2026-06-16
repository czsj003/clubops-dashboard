package com.clubops.player.dto;

import com.clubops.player.PlayerPosition;
import com.clubops.player.PlayerPositionType;

public record PlayerPositionResponse(
        Long id,
        PlayerPositionType positionType,
        Integer rating
) {
    public static PlayerPositionResponse from(PlayerPosition position) {
        return new PlayerPositionResponse(
                position.getId(),
                position.getPositionType(),
                position.getRating()
        );
    }
}