package com.banking.accountservice.controller;

import com.banking.accountservice.dtos.AccountRequestDto;
import com.banking.accountservice.dtos.AccountResponseDto;
import com.banking.accountservice.exception.ApiResponse;
import com.banking.accountservice.services.AccountServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountServices accountServices;

    @GetMapping
    public String greet(Authentication authentication) {
        return "Hello " + authentication.getName();
    }

    @PostMapping("/create-account")
    public ApiResponse<AccountResponseDto> createAccount(@RequestBody AccountRequestDto accountRequestDto) {
        return ApiResponse.success(200, "OK", accountServices.createAccount(accountRequestDto));
    }
}
