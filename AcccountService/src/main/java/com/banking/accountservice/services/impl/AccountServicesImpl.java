package com.banking.accountservice.services.impl;

import com.banking.accountservice.clientFeign.UserClient;
import com.banking.accountservice.config.CurrentUser;
import com.banking.accountservice.dtos.AccountRequestDto;
import com.banking.accountservice.dtos.AccountResponseDto;
import com.banking.accountservice.dtos.BalanceResponseDto;
import com.banking.accountservice.dtos.external.UserResponseDto;
import com.banking.accountservice.enums.Status;
import com.banking.accountservice.models.Account;
import com.banking.accountservice.models.AccountDetails;
import com.banking.accountservice.repo.AccountDetailsRepo;
import com.banking.accountservice.repo.AccountRepo;
import com.banking.accountservice.services.AccountServices;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    public AccountResponseDto updateAccountStatus(String accountNumber, Status status) {

        return null;
    }

    @Override
    public AccountResponseDto closeAccount(String email, String password, String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Cannot find account by accountNumber: " + accountNumber));

        Boolean authentication = userClient.authenticate(email, password).getBody();

        if(!authentication)
            throw new BadCredentialsException("Bad Credentials");
        account.setStatus(Status.CLOSED);

        accountRepo.save(account);

        return toDto(account,  account.getAccountDetails());
    }

    @Override
    public List<AccountResponseDto> getMyAccounts(Long userId) {
        List<Account> accounts = accountRepo.findByUserId(userId);
        return accounts.stream()
                .map(account -> toDto(account, account.getAccountDetails()))
                .collect(Collectors.toList());
    }

    @Override
    public BalanceResponseDto getBalanceByAccountNumber(Long userId, String accountNumber) {
        CurrentUser user = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Check if the user ID or account number is passed as null
        if(accountNumber==null || userId == null)
            return null;
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Cannot find account with accountNumber: " + accountNumber));
        if(!userId.equals(account.getUserId()))
            throw new BadCredentialsException("Only Account holder can Inquire Balance!!!");
        return BalanceResponseDto.builder()
                .accountId(account.getAccountId())
                .accountNumber(accountNumber)
                .accountHolderName(user.firstName() + " " + user.lastName())
                .currentBalance(account.getBalance())
                .build();
    }

    public AccountResponseDto toDto(Account account, AccountDetails accountDetails) {
        if(account == null || accountDetails == null)
            return null;

        UserResponseDto userResponseDto = userClient.getUserById(account.getUserId()).getBody();
        return AccountResponseDto.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(userResponseDto.getFullName())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .createdDate(account.getCreatedAt())
                .currency(accountDetails.getCurrency())
                .isKycVerified(accountDetails.getIsKycVerified())
                .build();
    }

    public String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%012d", ThreadLocalRandom.current().nextLong(1_000_000_000_000L));
        } while (accountRepo.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
