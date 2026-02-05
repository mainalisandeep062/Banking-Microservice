package com.banking.accountservice.controller;

import com.banking.accountservice.config.CurrentUser;
import com.banking.accountservice.dtos.AccountRequestDto;
import com.banking.accountservice.dtos.AccountResponseDto;
import com.banking.accountservice.dtos.BalanceResponseDto;
import com.banking.accountservice.enums.Status;
import com.banking.accountservice.exception.ApiResponse;
import com.banking.accountservice.services.AccountServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountServices accountServices;

    @PostMapping("/create-account")
    public ApiResponse<AccountResponseDto> createAccount(@RequestBody AccountRequestDto accountRequestDto) {
        return ApiResponse.success(200, "OK", accountServices.createAccount(accountRequestDto));
    }

    @GetMapping("/my-account")
    public ApiResponse<List<AccountResponseDto>> getMyAccounts(@AuthenticationPrincipal CurrentUser currentUser) {
        return ApiResponse.success(200, "Ok", accountServices.getMyAccounts(currentUser.userId()) );
    }

    @GetMapping("/balance")
    public ApiResponse<BalanceResponseDto> getBalance(@RequestParam String accountNumber,
                                                      @AuthenticationPrincipal CurrentUser user) {
        return ApiResponse.success(200, "OK", accountServices.getBalanceByAccountNumber(user.userId(), accountNumber));
    }

    @PostMapping("/update")
    public ApiResponse<AccountResponseDto> updateAccountStatus(@RequestParam Status status,
                                                               @RequestParam String accountNumber) {
        return ApiResponse.success(200, "OK", accountServices.updateAccountStatus(accountNumber, status));
    }

    @PostMapping("/close-account")
    public ApiResponse<AccountResponseDto> CloseAccount(@RequestParam String email,
                                                        @RequestParam String password,
                                                        @RequestParam String accountNumber){
        return ApiResponse.success(200,
                "Account Closed Successfully!!",
                accountServices.closeAccount(email, password, accountNumber));
    }
}
