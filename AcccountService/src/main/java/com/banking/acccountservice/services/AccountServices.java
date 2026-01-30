package com.banking.acccountservice.services;

import com.banking.acccountservice.dtos.AccountRequestDto;
import com.banking.acccountservice.dtos.AccountResponseDto;
import com.banking.acccountservice.dtos.AccountUpdateDto;

public interface AccountServices {
    AccountResponseDto createAccount(AccountRequestDto accountRequestDto);

    AccountResponseDto getAccountByUserId(Long userId);

    AccountResponseDto getAccountByAccountNumber(String accountNumber);

    AccountResponseDto updateAccount(AccountUpdateDto accountUpdateDto);

    AccountResponseDto closeAccount(AccountRequestDto accountRequestDto);
}
