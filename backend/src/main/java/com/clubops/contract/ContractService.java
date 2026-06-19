package com.clubops.contract;

import com.clubops.club.Club;
import com.clubops.club.ClubRepository;
import com.clubops.contract.dto.ContractBonusResponse;
import com.clubops.contract.dto.ContractListItemResponse;
import com.clubops.contract.dto.PlayerContractResponse;
import com.clubops.player.Player;
import com.clubops.player.PlayerRepository;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;
    private final PlayerContractRepository playerContractRepository;
    private final ContractBonusRepository contractBonusRepository;

    public List<ContractListItemResponse> getCurrentUserContracts(User user) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        return playerContractRepository
                .findByClubOrderByTeamDisplayOrderAscPlayerDisplayNameAsc(club)
                .stream()
                .map(ContractListItemResponse::from)
                .toList();
    }

    public PlayerContractResponse getPlayerContract(User user, Long playerId) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        Player player = playerRepository.findWithClubAndTeamById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        if (!player.getClub().getId().equals(club.getId())) {
            throw new RuntimeException("You do not have access to this player");
        }

        PlayerContract contract = playerContractRepository.findByPlayer(player)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        List<ContractBonusResponse> bonuses = contractBonusRepository
                .findByContractOrderByBonusTypeAsc(contract)
                .stream()
                .map(ContractBonusResponse::from)
                .toList();

        return PlayerContractResponse.from(contract, bonuses);
    }
}
