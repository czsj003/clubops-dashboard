package com.clubops.player;

import org.springframework.stereotype.Component;

@Component
public class PlayerPersonalityCalculator {

    public PlayerPersonality calculate(PlayerAttribute a) {
        int pro = a.getProfessionalism();
        int pre = a.getPressure();
        int amb = a.getAmbition();
        int tem = a.getTemperament();
        int loy = a.getLoyalty();
        int spo = a.getSportsmanship();
        int det = a.getDetermination();
        int con = a.getControversy();
        int leadership = a.getLeadership();

        if (between(pro, 15, 20) && between(pre, 14, 20) && between(amb, 12, 20)
                && between(tem, 15, 20) && between(loy, 15, 20)
                && between(spo, 15, 20) && between(det, 14, 20)) {
            return PlayerPersonality.MODEL_CITIZEN;
        }

        if (between(pro, 14, 20) && between(amb, 14, 20)
                && between(tem, 1, 9) && between(det, 14, 20)) {
            return PlayerPersonality.PERFECTIONIST;
        }

        if (between(pro, 15, 20) && between(pre, 1, 16)
                && between(amb, 5, 20) && between(spo, 12, 20)
                && between(det, 15, 17)) {
            return PlayerPersonality.RESOLUTE;
        }

        if (pro == 20 && between(tem, 10, 20)) {
            return PlayerPersonality.MODEL_PROFESSIONAL;
        }

        if (between(pro, 18, 19) && between(tem, 10, 20)) {
            return PlayerPersonality.PROFESSIONAL;
        }

        if (between(pro, 15, 20) && between(det, 1, 14)) {
            return PlayerPersonality.FAIRLY_PROFESSIONAL;
        }

        if (between(pro, 11, 17) && between(pre, 15, 20)
                && between(tem, 10, 20) && between(spo, 1, 14)
                && between(det, 1, 17)) {
            return PlayerPersonality.SPIRITED;
        }

        if (between(amb, 12, 20) && between(det, 17, 20)) {
            return PlayerPersonality.DRIVEN;
        }

        if (between(amb, 1, 11) && between(det, 17, 20)) {
            return PlayerPersonality.DETERMINED;
        }

        if (between(pro, 1, 14) && between(pre, 1, 16)
                && between(spo, 5, 20) && between(det, 15, 17)) {
            return PlayerPersonality.FAIRLY_DETERMINED;
        }

        if (pre == 20 && between(spo, 5, 20) && between(det, 15, 17)) {
            return PlayerPersonality.IRON_WILLED;
        }

        if (between(pre, 17, 20) && between(amb, 8, 20)
                && between(spo, 5, 20) && between(det, 15, 17)) {
            return PlayerPersonality.RESILIENT;
        }

        if (between(tem, 18, 20) && between(spo, 18, 20)
                && between(leadership, 18, 20)) {
            return PlayerPersonality.CHARISMATIC_LEADER;
        }

        if (det == 20 && leadership == 20) {
            return PlayerPersonality.BORN_LEADER;
        }

        if (between(leadership, 19, 20)) {
            return PlayerPersonality.LEADER;
        }

        if (amb == 20 && between(loy, 1, 10) && between(det, 1, 17)) {
            return PlayerPersonality.VERY_AMBITIOUS;
        }

        if (between(amb, 16, 20) && between(loy, 1, 10) && between(det, 1, 17)) {
            return PlayerPersonality.AMBITIOUS;
        }

        if (between(pro, 1, 14) && between(amb, 15, 20) && between(det, 1, 14)) {
            return PlayerPersonality.FAIRLY_AMBITIOUS;
        }

        if (between(pro, 1, 14) && between(amb, 15, 20)
                && between(loy, 1, 14) && between(det, 1, 14)) {
            return PlayerPersonality.FICKLE;
        }

        if (between(amb, 16, 20) && between(loy, 1, 3) && between(det, 1, 17)) {
            return PlayerPersonality.MERCENARY;
        }

        if (between(pro, 1, 10) && between(pre, 15, 20)
                && between(tem, 10, 20) && between(spo, 1, 14)
                && between(det, 1, 17)) {
            return PlayerPersonality.JOVIAL;
        }

        if (between(pro, 1, 17) && between(pre, 15, 20)
                && between(tem, 10, 20) && between(spo, 15, 20)
                && between(det, 1, 17)) {
            return PlayerPersonality.LIGHT_HEARTED;
        }

        if (between(amb, 5, 8) && loy == 20 && between(det, 18, 20)) {
            return PlayerPersonality.VERY_LOYAL;
        }

        if (between(amb, 1, 7) && between(loy, 17, 19) && between(det, 6, 17)) {
            return PlayerPersonality.LOYAL;
        }

        if (between(pro, 1, 14) && between(amb, 6, 14)
                && between(loy, 15, 20) && between(det, 1, 14)) {
            return PlayerPersonality.FAIRLY_LOYAL;
        }

        if (between(pro, 5, 20) && spo == 20 && between(det, 1, 10)) {
            return PlayerPersonality.HONEST;
        }

        if (between(pro, 8, 20) && between(spo, 18, 20) && between(det, 1, 10)) {
            return PlayerPersonality.SPORTING;
        }

        if (between(pro, 1, 14) && between(amb, 1, 14)
                && between(loy, 1, 14) && between(spo, 15, 20)
                && between(det, 1, 14)) {
            return PlayerPersonality.FAIRLY_SPORTING;
        }

        if (spo == 1 && between(det, 10, 17)) {
            return PlayerPersonality.UNSPORTING;
        }

        if (between(spo, 1, 4) && between(det, 10, 17)) {
            return PlayerPersonality.REALIST;
        }

        if (between(pro, 1, 14) && between(amb, 1, 14)
                && between(loy, 1, 14) && between(spo, 1, 14)
                && between(det, 1, 14)) {
            return PlayerPersonality.BALANCED;
        }

        if (pro == 1 && between(tem, 5, 20) && between(det, 1, 9)) {
            return PlayerPersonality.SLACK;
        }

        if (between(pro, 2, 4) && between(tem, 5, 20) && between(det, 1, 9)) {
            return PlayerPersonality.CASUAL;
        }

        if (between(pro, 1, 10) && between(tem, 1, 4)) {
            return PlayerPersonality.TEMPERAMENTAL;
        }

        if (between(pro, 5, 20) && between(amb, 1, 10)
                && between(spo, 1, 17) && det == 1) {
            return PlayerPersonality.EASILY_DISCOURAGED;
        }

        if (between(pro, 5, 20) && between(amb, 1, 10)
                && between(spo, 1, 17) && between(det, 2, 5)) {
            return PlayerPersonality.LOW_DETERMINATION;
        }

        if (between(pro, 5, 20) && between(spo, 1, 17) && between(det, 1, 10)) {
            return PlayerPersonality.SPINELESS;
        }

        if (between(pro, 5, 20) && between(pre, 2, 3)
                && between(spo, 1, 17) && between(det, 1, 10)) {
            return PlayerPersonality.LOW_SELF_BELIEF;
        }

        if (between(amb, 1, 5) && between(loy, 11, 20) && between(det, 1, 17)) {
            return PlayerPersonality.UNAMBITIOUS;
        }

        return PlayerPersonality.BALANCED;
    }

    private boolean between(int value, int min, int max) {
        return value >= min && value <= max;
    }
}