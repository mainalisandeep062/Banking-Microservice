package com.banking.accountservice.services.impl;

import com.banking.accountservice.clientFeign.UserClient;
import com.banking.accountservice.config.CurrentUser;
import com.banking.accountservice.dtos.AccountRequestDto;
import com.banking.accountservice.dtos.AccountResponseDto;
import com.banking.accountservice.dtos.AccountSyncNotificationDto;
import com.banking.accountservice.dtos.BalanceResponseDto;
import com.banking.accountservice.dtos.mirror.user.UserResponseDto;
import com.banking.accountservice.enums.Status;
import com.banking.accountservice.models.Account;
import com.banking.accountservice.models.AccountDetails;
import com.banking.accountservice.repo.AccountDetailsRepo;
import com.banking.accountservice.repo.AccountRepo;
import com.banking.accountservice.services.AccountServices;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServicesImpl implements AccountServices {

    private final AccountDetailsRepo accountDetailsRepo;
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepo accountRepo;
    private final UserClient userClient;

    @Transactional
    @Override
    public AccountResponseDto createAccount(AccountRequestDto accountRequestDto) {
        if(accountRequestDto == null)
            return null;
        boolean userExists = userClient.checkIfUserExists(accountRequestDto.getUserId()).getBody();

        if (!userExists) {
            throw new RuntimeException("Failure Creating Account!! User with ID " + accountRequestDto.getUserId() + " not found!");
        }
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .userId(accountRequestDto.getUserId())
                .accountType(accountRequestDto.getAccountType())
                .balance(BigDecimal.ZERO)
                .totalWithdrawToday(BigDecimal.ZERO)
                .status(Status.ACTIVE)
                .maturityDate(accountRequestDto.getMaturityDate())
                .build();
        accountRepo.save(account);

        try {
            AccountSyncNotificationDto syncDto = AccountSyncNotificationDto.builder()
                    .accountId(account.getAccountId())
                    .userId(account.getUserId())
                    .accountNumber(account.getAccountNumber())
                    .accountType(account.getAccountType())
                    .notificationType("ACCOUNT_CREATED")
                    .build();
            rabbitTemplate.convertAndSend(
                    "banking.direct.exchange",
                    "account.sync.key",
                    syncDto);
        } catch (Exception ex) {
            log.error("Failed to send account creation notification: {}", ex.getMessage());
        }

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

        // Fetch name once for the response (admin may create for another user)
        UserResponseDto userDto = userClient.getUserById(account.getUserId()).getBody();
        String holderName = userDto != null ? userDto.getFullName() : "";
        return toDto(account, details, holderName);
    }

    @Override
    public AccountResponseDto updateAccountStatus(String accountNumber, Status status) {

        return null;
    }

    @Transactional
    @Override
    public AccountResponseDto closeAccount(String email, String password, String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Cannot find account by accountNumber: " + accountNumber));

        Boolean authentication = userClient.authenticate(Map.of("email", email, "password", password)).getBody();

        if(!authentication)
            throw new BadCredentialsException("Bad Credentials");
        account.setStatus(Status.CLOSED);

        accountRepo.save(account);

        try{
            AccountSyncNotificationDto syncDto = AccountSyncNotificationDto.builder()
                    .accountId(account.getAccountId())
                    .userId(account.getUserId())
                    .accountNumber(account.getAccountNumber())
                    .accountType(account.getAccountType())
                    .notificationType("ACCOUNT_CLOSED")
                    .build();
            rabbitTemplate.convertAndSend("banking.direct.exchange",
                    "account.sync.key",
                    syncDto);
        }catch(Exception ex){
            log.error("Failed to send account closure notification: {}", ex.getMessage());
        }

        return toDto(account, account.getAccountDetails(), resolveCurrentUserName());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountResponseDto> getMyAccounts(Long userId) {
        String holderName = resolveCurrentUserName();
        List<Account> accounts = accountRepo.findByUserId(userId);
        return accounts.stream()
                .map(account -> toDto(account, account.getAccountDetails(), holderName))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public BalanceResponseDto getBalanceByAccountNumber(Long userId, String accountNumber) {
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
                .accountHolderName(resolveCurrentUserName())
                .currentBalance(account.getBalance())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public AccountResponseDto getAccountByAccountNumber(Long userId, String accountNumber) {
        if(accountNumber==null)
            return null;
        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Cannot find account with accountNumber: " + accountNumber));

        if(!userId.equals(account.getUserId()))
            throw new BadCredentialsException("Only Account holder can Inquire Account Details!!!");
        return toDto(account, account.getAccountDetails(), resolveCurrentUserName());
    }

    private String resolveCurrentUserName() {
        CurrentUser user = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.firstName() + " " + user.lastName();
    }

    public AccountResponseDto toDto(Account account, AccountDetails accountDetails, String accountHolderName) {
        if(account == null || accountDetails == null)
            return null;

        return AccountResponseDto.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountHolderName(accountHolderName)
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
