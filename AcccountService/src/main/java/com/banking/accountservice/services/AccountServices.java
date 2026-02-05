package com.banking.accountservice.services;

import com.banking.accountservice.dtos.AccountRequestDto;
import com.banking.accountservice.dtos.AccountResponseDto;
import com.banking.accountservice.dtos.BalanceResponseDto;
import com.banking.accountservice.enums.Status;

import java.util.List;

public interface AccountServices {
    AccountResponseDto createAccount(AccountRequestDto accountRequestDto);

    AccountResponseDto updateAccountStatus(String accountNumber, Status status);

    AccountResponseDto closeAccount(String email, String password, String accountNumber);

    List<AccountResponseDto> getMyAccounts(Long userId);

    BalanceResponseDto getBalanceByAccountNumber(Long userId, String accountNumber);

}
