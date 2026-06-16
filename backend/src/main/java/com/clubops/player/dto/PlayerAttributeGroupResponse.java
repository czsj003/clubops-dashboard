package com.clubops.player.dto;

import com.clubops.player.PlayerAttribute;

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
        return new PlayerAttributeGroupResponse(
                Map.of(
                        "currentAbility", a.getCurrentAbility(),
                        "potentialAbility", a.getPotentialAbility()
                ),
                Map.of(
                        "currentReputation", a.getCurrentReputation(),
                        "homeReputation", a.getHomeReputation(),
                        "worldReputation", a.getWorldReputation()
                ),
                Map.of(
                        "leftFoot", a.getLeftFoot(),
                        "rightFoot", a.getRightFoot()
                ),
                Map.of(
                        "adaptability", a.getAdaptability(),
                        "ambition", a.getAmbition(),
                        "controversy", a.getControversy(),
                        "loyalty", a.getLoyalty(),
                        "pressure", a.getPressure(),
                        "professionalism", a.getProfessionalism(),
                        "sportsmanship", a.getSportsmanship(),
                        "temperament", a.getTemperament()
                ),
                Map.ofEntries(
                        Map.entry("aggression", a.getAggression()),
                        Map.entry("anticipation", a.getAnticipation()),
                        Map.entry("bravery", a.getBravery()),
                        Map.entry("composure", a.getComposure()),
                        Map.entry("concentration", a.getConcentration()),
                        Map.entry("consistency", a.getConsistency()),
                        Map.entry("decisions", a.getDecisions()),
                        Map.entry("determination", a.getDetermination()),
                        Map.entry("dirtiness", a.getDirtiness()),
                        Map.entry("flair", a.getFlair()),
                        Map.entry("importantMatches", a.getImportantMatches()),
                        Map.entry("leadership", a.getLeadership()),
                        Map.entry("movement", a.getMovement()),
                        Map.entry("positioning", a.getPositioning()),
                        Map.entry("teamWork", a.getTeamWork()),
                        Map.entry("vision", a.getVision()),
                        Map.entry("workRate", a.getWorkRate())
                ),
                Map.ofEntries(
                        Map.entry("acceleration", a.getAcceleration()),
                        Map.entry("agility", a.getAgility()),
                        Map.entry("balance", a.getBalance()),
                        Map.entry("injuryProneness", a.getInjuryProneness()),
                        Map.entry("jumpingReach", a.getJumpingReach()),
                        Map.entry("naturalFitness", a.getNaturalFitness()),
                        Map.entry("pace", a.getPace()),
                        Map.entry("stamina", a.getStamina()),
                        Map.entry("strength", a.getStrength())
                ),
                Map.ofEntries(
                        Map.entry("corners", a.getCorners()),
                        Map.entry("crossing", a.getCrossing()),
                        Map.entry("dribbling", a.getDribbling()),
                        Map.entry("finishing", a.getFinishing()),
                        Map.entry("firstTouch", a.getFirstTouch()),
                        Map.entry("freeKicks", a.getFreeKicks()),
                        Map.entry("heading", a.getHeading()),
                        Map.entry("longShots", a.getLongShots()),
                        Map.entry("longThrows", a.getLongThrows()),
                        Map.entry("marking", a.getMarking()),
                        Map.entry("passing", a.getPassing()),
                        Map.entry("penaltyTaking", a.getPenaltyTaking()),
                        Map.entry("tackling", a.getTackling()),
                        Map.entry("technique", a.getTechnique()),
                        Map.entry("versatility", a.getVersatility())
                ),
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
}