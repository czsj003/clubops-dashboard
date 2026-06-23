package com.clubops.player;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NationalityLanguageServiceTests {

    private final NationalityLanguageService service =
            new NationalityLanguageService();

    @Test
    void mapsRepresentativeNationalitiesToNativeLanguages() {
        assertThat(service.getDefaultLanguage(CountryCode.ENGLAND))
                .isEqualTo(LanguageCode.ENGLISH);
        assertThat(service.getDefaultLanguage(CountryCode.CHINA))
                .isEqualTo(LanguageCode.CHINESE);
        assertThat(service.getDefaultLanguage(CountryCode.TURKEY))
                .isEqualTo(LanguageCode.TURKISH);
        assertThat(service.getDefaultLanguage(CountryCode.BRAZIL))
                .isEqualTo(LanguageCode.PORTUGUESE);
        assertThat(service.getDefaultLanguage(CountryCode.SOUTH_KOREA))
                .isEqualTo(LanguageCode.KOREAN);
    }
}
