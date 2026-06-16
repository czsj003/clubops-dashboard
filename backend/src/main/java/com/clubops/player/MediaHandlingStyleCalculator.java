package com.clubops.player;

import org.springframework.stereotype.Component;

@Component
public class MediaHandlingStyleCalculator {

    public MediaHandlingStyle calculate(PlayerAttribute a) {
        int pro = a.getProfessionalism();
        int pre = a.getPressure();
        int tem = a.getTemperament();
        int loy = a.getLoyalty();
        int spo = a.getSportsmanship();
        int con = a.getControversy();

        if (between(tem, 1, 4) && between(spo, 1, 4)) {
            return MediaHandlingStyle.CONFRONTATIONAL;
        }

        if (between(pro, 15, 20) && between(pre, 15, 20)
                && between(tem, 7, 14) && between(con, 6, 14)) {
            return MediaHandlingStyle.EVASIVE;
        }

        if (between(tem, 7, 20) && between(loy, 11, 20)
                && between(con, 1, 14)) {
            return MediaHandlingStyle.LEVEL_HEADED;
        }

        if (between(con, 1, 14)) {
            return MediaHandlingStyle.MEDIA_FRIENDLY;
        }

        if (between(con, 15, 20)) {
            return MediaHandlingStyle.OUTSPOKEN;
        }

        if (between(pro, 15, 20) && between(tem, 7, 20)
                && between(con, 1, 5)) {
            return MediaHandlingStyle.RESERVED;
        }

        if (between(tem, 1, 4)) {
            return MediaHandlingStyle.SHORT_TEMPERED;
        }

        if (between(pre, 15, 20) && between(tem, 15, 20)) {
            return MediaHandlingStyle.UNFLAPPABLE;
        }

        if (between(tem, 1, 6)) {
            return MediaHandlingStyle.VOLATILE;
        }

        return MediaHandlingStyle.MEDIA_FRIENDLY;
    }

    private boolean between(int value, int min, int max) {
        return value >= min && value <= max;
    }
}