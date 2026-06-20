package com.clubops.contract;

import com.clubops.club.Country;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class ReleaseClausePolicyService {

    private static final Set<Country> REQUIRED_COUNTRIES = Set.of(
            Country.SPAIN,
            Country.PORTUGAL,
            Country.BRAZIL,
            Country.ARGENTINA
    );

    public ReleaseClauseRule getRule(Country country) {
        if (REQUIRED_COUNTRIES.contains(country)) {
            return ReleaseClauseRule.REQUIRED;
        }
        if (country == Country.FRANCE) {
            return ReleaseClauseRule.FORBIDDEN;
        }
        return ReleaseClauseRule.OPTIONAL;
    }

    public void validate(Country country, BigDecimal releaseClauseAmount) {
        ReleaseClauseRule rule = getRule(country);
        boolean hasPositiveAmount = releaseClauseAmount != null
                && releaseClauseAmount.compareTo(BigDecimal.ZERO) > 0;

        if (rule == ReleaseClauseRule.REQUIRED && !hasPositiveAmount) {
            throw new IllegalArgumentException(
                    "Release clause is required for clubs in " + country
            );
        }

        if (rule == ReleaseClauseRule.FORBIDDEN && hasPositiveAmount) {
            throw new IllegalArgumentException(
                    "Release clause is forbidden for clubs in " + country
            );
        }
    }
}
