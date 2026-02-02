package com.banking.accountservice.services;

import com.banking.accountservice.dtos.AccountRequestDto;
import com.banking.accountservice.dtos.AccountResponseDto;
import com.banking.accountservice.dtos.AccountUpdateDto;

public interface AccountServices {
    AccountResponseDto createAccount(AccountRequestDto accountRequestDto);

    AccountResponseDto getAccountByUserId(Long userId);

    AccountResponseDto getAccountByAccountNumber(String accountNumber);

    AccountResponseDto updateAccount(AccountUpdateDto accountUpdateDto);

    AccountResponseDto closeAccount(AccountRequestDto accountRequestDto);
}
