package com.banking.accountservice.services;

import com.banking.accountservice.dtos.AccountRequestDto;
import com.banking.accountservice.dtos.AccountResponseDto;
import com.banking.accountservice.enums.Status;

public interface AccountServices {
    AccountResponseDto createAccount(AccountRequestDto accountRequestDto);

    AccountResponseDto getAccountByUserId(Long userId);

    AccountResponseDto getAccountByAccountNumber(String accountNumber);

    AccountResponseDto updateAccountStatus(String accountNumber, Status status);

    AccountResponseDto closeAccount(String email, String password, String accountNumber);
}
