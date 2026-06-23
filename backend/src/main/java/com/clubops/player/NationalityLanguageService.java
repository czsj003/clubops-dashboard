package com.clubops.player;

import org.springframework.stereotype.Service;

@Service
public class NationalityLanguageService {

    public LanguageCode getDefaultLanguage(CountryCode nationality) {
        if (nationality == null) {
            return LanguageCode.ENGLISH;
        }

        return switch (nationality) {
            case ENGLAND, SCOTLAND, WALES, NORTHERN_IRELAND, IRELAND,
                 USA, CANADA, NIGERIA, GHANA, OTHER -> LanguageCode.ENGLISH;
            case SPAIN, ARGENTINA, MEXICO -> LanguageCode.SPANISH;
            case ITALY -> LanguageCode.ITALIAN;
            case GERMANY -> LanguageCode.GERMAN;
            case FRANCE, BELGIUM, SENEGAL -> LanguageCode.FRENCH;
            case PORTUGAL, BRAZIL -> LanguageCode.PORTUGUESE;
            case NETHERLANDS -> LanguageCode.DUTCH;
            case TURKEY -> LanguageCode.TURKISH;
            case SAUDI_ARABIA, MOROCCO -> LanguageCode.ARABIC;
            case CHINA -> LanguageCode.CHINESE;
            case JAPAN -> LanguageCode.JAPANESE;
            case SOUTH_KOREA -> LanguageCode.KOREAN;
        };
    }
}
