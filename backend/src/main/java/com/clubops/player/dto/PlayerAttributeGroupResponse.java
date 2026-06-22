package com.clubops.player.dto;

import com.clubops.player.PlayerAttribute;

import java.util.LinkedHashMap;
import java.util.Map;

public record PlayerAttributeGroupResponse(
        Map<String, Integer> ability,
        Map<String, Integer> reputation,
        Map<String, Integer> feet,
        Map<String, Integer> personal,
        Map<String, Integer> mental,
        Map<String, Integer> physical,
        Map<String, Integer> technical,
        Map<String, Integer> goalkeeping
) {
    public static PlayerAttributeGroupResponse from(PlayerAttribute a) {
        return from(a, true);
    }

    public static PlayerAttributeGroupResponse from(
            PlayerAttribute a,
            boolean includeHidden
    ) {
        return new PlayerAttributeGroupResponse(
                includeHidden ? Map.of(
                        "currentAbility", a.getCurrentAbility(),
                        "potentialAbility", a.getPotentialAbility()
                ) : Map.of(),
                includeHidden ? Map.of(
                        "currentReputation", a.getCurrentReputation(),
                        "homeReputation", a.getHomeReputation(),
                        "worldReputation", a.getWorldReputation()
                ) : Map.of(),
                Map.of(
                        "leftFoot", a.getLeftFoot(),
                        "rightFoot", a.getRightFoot()
                ),
                includeHidden ? Map.of(
                        "adaptability", a.getAdaptability(),
                        "ambition", a.getAmbition(),
                        "controversy", a.getControversy(),
                        "loyalty", a.getLoyalty(),
                        "pressure", a.getPressure(),
                        "professionalism", a.getProfessionalism(),
                        "sportsmanship", a.getSportsmanship(),
                        "temperament", a.getTemperament()
                ) : Map.of(),
                mentalAttributes(a, includeHidden),
                physicalAttributes(a, includeHidden),
                technicalAttributes(a, includeHidden),
                Map.ofEntries(
                        Map.entry("aerialAbility", a.getAerialAbility()),
                        Map.entry("commandOfArea", a.getCommandOfArea()),
                        Map.entry("communication", a.getCommunication()),
                        Map.entry("eccentricity", a.getEccentricity()),
                        Map.entry("handling", a.getHandling()),
                        Map.entry("kicking", a.getKicking()),
                        Map.entry("oneOnOnes", a.getOneOnOnes()),
                        Map.entry("reflexes", a.getReflexes()),
                        Map.entry("rushingOut", a.getRushingOut()),
                        Map.entry("tendencyToPunch", a.getTendencyToPunch()),
                        Map.entry("throwing", a.getThrowing())
                )
        );
    }

    private static Map<String, Integer> mentalAttributes(
            PlayerAttribute a,
            boolean includeHidden
    ) {
        Map<String, Integer> values = new LinkedHashMap<>();
        values.put("aggression", a.getAggression());
        values.put("anticipation", a.getAnticipation());
        values.put("bravery", a.getBravery());
        values.put("composure", a.getComposure());
        values.put("concentration", a.getConcentration());
        values.put("decisions", a.getDecisions());
        values.put("determination", a.getDetermination());
        values.put("flair", a.getFlair());
        values.put("leadership", a.getLeadership());
        values.put("movement", a.getMovement());
        values.put("positioning", a.getPositioning());
        values.put("teamWork", a.getTeamWork());
        values.put("vision", a.getVision());
        values.put("workRate", a.getWorkRate());

        if (includeHidden) {
            values.put("consistency", a.getConsistency());
            values.put("dirtiness", a.getDirtiness());
            values.put("importantMatches", a.getImportantMatches());
        }

        return Map.copyOf(values);
    }

    private static Map<String, Integer> physicalAttributes(
            PlayerAttribute a,
            boolean includeHidden
    ) {
        Map<String, Integer> values = new LinkedHashMap<>();
        values.put("acceleration", a.getAcceleration());
        values.put("agility", a.getAgility());
        values.put("balance", a.getBalance());
        values.put("jumpingReach", a.getJumpingReach());
        values.put("naturalFitness", a.getNaturalFitness());
        values.put("pace", a.getPace());
        values.put("stamina", a.getStamina());
        values.put("strength", a.getStrength());

        if (includeHidden) {
            values.put("injuryProneness", a.getInjuryProneness());
        }

        return Map.copyOf(values);
    }

    private static Map<String, Integer> technicalAttributes(
            PlayerAttribute a,
            boolean includeHidden
    ) {
        Map<String, Integer> values = new LinkedHashMap<>();
        values.put("corners", a.getCorners());
        values.put("crossing", a.getCrossing());
        values.put("dribbling", a.getDribbling());
        values.put("finishing", a.getFinishing());
        values.put("firstTouch", a.getFirstTouch());
        values.put("freeKicks", a.getFreeKicks());
        values.put("heading", a.getHeading());
        values.put("longShots", a.getLongShots());
        values.put("longThrows", a.getLongThrows());
        values.put("marking", a.getMarking());
        values.put("passing", a.getPassing());
        values.put("penaltyTaking", a.getPenaltyTaking());
        values.put("tackling", a.getTackling());
        values.put("technique", a.getTechnique());

        if (includeHidden) {
            values.put("versatility", a.getVersatility());
        }

        return Map.copyOf(values);
    }
}
