package com.clubops.finance;

import com.clubops.finance.dto.FinanceSummaryResponse;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping
    public FinanceSummaryResponse getFinance(
            @AuthenticationPrincipal User user
    ) {
        return financeService.getCurrentUserFinance(user);
    }
}
