package com.clubops.contract;

import com.clubops.club.Club;
import com.clubops.club.ClubRepository;
import com.clubops.contract.dto.ContractListItemResponse;
import com.clubops.contract.dto.PlayerContractResponse;
import com.clubops.contract.dto.ReleaseClausePolicyResponse;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ClubRepository clubRepository;
    private final ReleaseClausePolicyService releaseClausePolicyService;

    @GetMapping
    public List<ContractListItemResponse> getContracts(
            @AuthenticationPrincipal User user
    ) {
        return contractService.getCurrentUserContracts(user);
    }

    @GetMapping("/release-clause-policy")
    public ReleaseClausePolicyResponse getReleaseClausePolicy(
            @AuthenticationPrincipal User user
    ) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        return new ReleaseClausePolicyResponse(
                releaseClausePolicyService.getRule(club.getCountry())
        );
    }

    @GetMapping("/{playerId}")
    public PlayerContractResponse getPlayerContract(
            @AuthenticationPrincipal User user,
            @PathVariable Long playerId
    ) {
        return contractService.getPlayerContract(user, playerId);
    }
}
