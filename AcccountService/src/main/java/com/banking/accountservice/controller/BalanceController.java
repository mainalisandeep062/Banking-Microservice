package com.banking.accountservice.controller;

import com.banking.accountservice.dtos.CriticalResponseDto;
import com.banking.accountservice.exception.ApiResponse;
import com.banking.accountservice.services.AccountServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class BalanceController {
    private final AccountServices accountServices;

    @GetMapping("/balance")
    public ApiResponse<CriticalResponseDto> getBalance(@RequestParam String accountNumber) {
        return ApiResponse.success(200, "OK", null);
    }


}
