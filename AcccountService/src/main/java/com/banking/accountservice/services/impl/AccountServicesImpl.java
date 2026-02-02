package com.banking.accountservice.services.impl;

import com.banking.accountservice.clientFeign.UserClient;
import com.banking.accountservice.dtos.AccountRequestDto;
import com.banking.accountservice.dtos.AccountResponseDto;
import com.banking.accountservice.dtos.AccountUpdateDto;
import com.banking.accountservice.dtos.external.UserResponseDto;
import com.banking.accountservice.enums.Status;
import com.banking.accountservice.models.Account;
import com.banking.accountservice.models.AccountDetails;
import com.banking.accountservice.repo.AccountDetailsRepo;
import com.banking.accountservice.repo.AccountRepo;
import com.banking.accountservice.services.AccountServices;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServicesImpl implements AccountServices {

    private final AccountRepo accountRepo;
    private final AccountDetailsRepo accountDetailsRepo;
    private final UserClient userClient;

    @Transactional
    @Override
    public AccountResponseDto createAccount(AccountRequestDto accountRequestDto) {
        if(accountRequestDto == null)
            return null;
        boolean userExists = userClient.checkIfUserExists(accountRequestDto.getUserId()).getBody();

        if (!userExists) {
            throw new RuntimeException("Cannot create account: User with ID " + accountRequestDto.getUserId() + " not found!");
        }
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .userId(accountRequestDto.getUserId())
                .accountType(accountRequestDto.getAccountType())
                .balance(BigDecimal.ZERO)
                .status(Status.ACTIVE)
                .maturityDate(accountRequestDto.getMaturityDate())
                .build();
        accountRepo.save(account);
        AccountDetails details =AccountDetails.builder()
                        .account(account)
                        .currency(accountRequestDto.getCurrency())
                        .dailyWithdrawalLimit(accountRequestDto.getDailyWithdrawalLimit())
                        .perTransactionLimit(accountRequestDto.getPerTransactionLimit())
                        .nomineeName(accountRequestDto.getNomineeName())
                        .nomineeEmail(accountRequestDto.getNomineeEmail())
                        .nomineeRelationship(accountRequestDto.getNomineeRelationship())
                        .isKycVerified(true)
                        .build();
        accountDetailsRepo.save(details);

        return toDto(account, details);
    }

    @Override
    public AccountResponseDto getAccountByUserId(Long userId) {
        return null;
    }

    @Override
    public AccountResponseDto getAccountByAccountNumber(String accountNumber) {
        return null;
    }

    @Override
    public AccountResponseDto updateAccount(AccountUpdateDto accountUpdateDto) {
        return null;
    }

    @Override
    public AccountResponseDto closeAccount(AccountRequestDto accountRequestDto) {
        return null;
    }

    public AccountResponseDto toDto(Account account, AccountDetails accountDetails) {
        if(account == null || accountDetails == null)
            return null;
        UserResponseDto userResponseDto = userClient.getUserById(account.getUserId()).getBody();
        AccountResponseDto accountResponseDto = AccountResponseDto.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(userResponseDto.getFullName())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .createdDate(account.getCreatedAt())
                .currency(accountDetails.getCurrency())
                .isKycVerified(accountDetails.getIsKycVerified())
                .build();
        return accountResponseDto;
    }

    public String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%012d", ThreadLocalRandom.current().nextLong(1_000_000_000_000L));
        } while (accountRepo.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
