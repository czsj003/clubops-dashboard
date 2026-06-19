package com.clubops.player;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PlayerAbilityResolver {

    public ResolvedAbility resolve(
            LocalDate dateOfBirth,
            Map<String, Integer> attributes,
            String potentialMode,
            String negativePotentialLevel
    ) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes are required");
        }

        int inputCa = attributes.getOrDefault("currentAbility", 0);
        int inputPa = attributes.getOrDefault("potentialAbility", 0);
        int ca = resolveCa(inputCa);
        int pa;

        if ("NEGATIVE".equalsIgnoreCase(potentialMode)) {
            pa = resolveNegativePotential(dateOfBirth, ca, negativePotentialLevel);
        } else if ("RANDOM".equalsIgnoreCase(potentialMode) || inputPa == 0) {
            pa = random(ca, 200);
        } else {
            pa = resolveFixedPa(inputPa);
        }

        if (pa < ca) {
            pa = ca;
        }

        attributes.put("currentAbility", ca);
        attributes.put("potentialAbility", pa);

        return new ResolvedAbility(ca, pa);
    }

    private int resolveCa(int inputCa) {
        if (inputCa == 0) {
            return random(1, 200);
        }
        if (inputCa < 1 || inputCa > 200) {
            throw new IllegalArgumentException(
                    "CA must be between 0 and 200 when creating a player"
            );
        }

        return inputCa;
    }

    private int resolveFixedPa(int inputPa) {
        if (inputPa < 1 || inputPa > 200) {
            throw new IllegalArgumentException(
                    "PA must be between 0 and 200 when creating a player"
            );
        }

        return inputPa;
    }

    private int resolveNegativePotential(
            LocalDate dateOfBirth,
            int ca,
            String level
    ) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

        if (age >= 22) {
            throw new IllegalArgumentException(
                    "Negative potential is only allowed for players under 22"
            );
        }
        if (ca >= 170) {
            throw new IllegalArgumentException(
                    "Players with CA 170 or higher cannot use negative potential"
            );
        }

        PotentialRange range = rangeForLevel(level);

        if (range.max() < ca) {
            throw new IllegalArgumentException(
                    "Selected negative potential range cannot produce PA greater than or equal to CA"
            );
        }

        return random(Math.max(range.min(), ca), range.max());
    }

    private PotentialRange rangeForLevel(String level) {
        if (level == null || level.isBlank()) {
            throw new IllegalArgumentException("Negative potential level is required");
        }

        return switch (level) {
            case "-10" -> new PotentialRange(170, 200);
            case "-9.5" -> new PotentialRange(160, 190);
            case "-9" -> new PotentialRange(150, 180);
            case "-8.5" -> new PotentialRange(140, 170);
            case "-8" -> new PotentialRange(130, 160);
            case "-7.5" -> new PotentialRange(120, 150);
            case "-7" -> new PotentialRange(110, 140);
            case "-6.5" -> new PotentialRange(100, 130);
            case "-6" -> new PotentialRange(90, 120);
            case "-5.5" -> new PotentialRange(80, 110);
            case "-5" -> new PotentialRange(70, 100);
            case "-4.5" -> new PotentialRange(60, 90);
            case "-4" -> new PotentialRange(50, 80);
            case "-3.5" -> new PotentialRange(40, 70);
            case "-3" -> new PotentialRange(30, 60);
            case "-2.5" -> new PotentialRange(20, 50);
            case "-2" -> new PotentialRange(10, 40);
            case "-1.5" -> new PotentialRange(0, 30);
            case "-1" -> new PotentialRange(0, 20);
            default -> throw new IllegalArgumentException(
                    "Unknown negative potential level: " + level
            );
        };
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private record PotentialRange(int min, int max) {
    }

    public record ResolvedAbility(int currentAbility, int potentialAbility) {
    }
}
