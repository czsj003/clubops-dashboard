package com.clubops.contract.dto;

import com.clubops.contract.PlayerContract;
import com.clubops.contract.PlayerContractType;
import com.clubops.contract.WageDisplayPeriod;
import com.clubops.currency.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PlayerContractResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        PlayerContractType contractType,

        BigDecimal wageAmount,
        CurrencyCode wageCurrency,
        WageDisplayPeriod wageDisplayPeriod,

        Long teamId,
        String teamName,
        String teamType,

        Integer squadNumber,

        BigDecimal releaseClauseAmount,
        CurrencyCode releaseClauseCurrency,

        List<ContractBonusResponse> bonuses
) {
    public static PlayerContractResponse from(
            PlayerContract contract,
            List<ContractBonusResponse> bonuses
    ) {
        return new PlayerContractResponse(
                contract.getId(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getContractType(),

                contract.getWageAmount(),
                contract.getWageCurrency(),
                contract.getWageDisplayPeriod(),

                contract.getTeam().getId(),
                contract.getTeam().getName(),
                contract.getTeam().getType().name(),

                contract.getSquadNumber(),

                contract.getReleaseClauseAmount(),
                contract.getReleaseClauseCurrency(),

                bonuses
        );
    }
}