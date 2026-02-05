package com.banking.accountservice.controller;

import com.banking.accountservice.config.CurrentUser;
import com.banking.accountservice.dtos.BalanceResponseDto;
import com.banking.accountservice.exception.ApiResponse;
import com.banking.accountservice.services.AccountServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account/transaction")
@RequiredArgsConstructor
public class BalanceController {
    private final AccountServices accountServices;

    public ApiResponse<BalanceResponseDto> withdraw(@RequestParam String accountNumber) {
        return null;
    }
}
