package com.clubops.contract.dto;

import com.clubops.contract.PlayerContract;
import com.clubops.contract.PlayerContractType;
import com.clubops.contract.WageDisplayPeriod;
import com.clubops.currency.CurrencyCode;
import com.clubops.player.Player;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractListItemResponse(
        Long playerId,
        String playerName,
        String teamName,
        Integer squadNumber,
        LocalDate startDate,
        LocalDate endDate,
        PlayerContractType contractType,
        BigDecimal wageAmount,
        CurrencyCode wageCurrency,
        WageDisplayPeriod wageDisplayPeriod,
        BigDecimal releaseClauseAmount,
        CurrencyCode releaseClauseCurrency
) {
    public static ContractListItemResponse from(PlayerContract contract) {
        Player player = contract.getPlayer();

        return new ContractListItemResponse(
                player.getId(),
                player.getDisplayName(),
                contract.getTeam().getName(),
                contract.getSquadNumber(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getContractType(),
                contract.getWageAmount(),
                contract.getWageCurrency(),
                contract.getWageDisplayPeriod(),
                contract.getReleaseClauseAmount(),
                contract.getReleaseClauseCurrency()
        );
    }
}
