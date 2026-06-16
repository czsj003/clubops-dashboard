package com.clubops.player.dto;

import com.clubops.player.LanguageCode;
import com.clubops.player.PlayerLanguage;

public record PlayerLanguageResponse(
        LanguageCode languageCode,
        Integer fluency
) {
    public static PlayerLanguageResponse from(PlayerLanguage language) {
        return new PlayerLanguageResponse(
                language.getLanguageCode(),
                language.getFluency()
        );
    }
}