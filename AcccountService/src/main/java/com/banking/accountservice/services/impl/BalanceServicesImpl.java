package com.banking.accountservice.services.impl;

import com.banking.accountservice.dtos.mirror.transaction.DepositRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.TransactionResponseDto;
import com.banking.accountservice.dtos.mirror.transaction.TransferRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.WithdrawRequestDto;
import com.banking.accountservice.enums.TransactionType;
import com.banking.accountservice.models.Account;
import com.banking.accountservice.models.ProcessedTransaction;
import com.banking.accountservice.repo.AccountRepo;
import com.banking.accountservice.repo.ProcessedTransactionRepo;
import com.banking.accountservice.services.BalanceServices;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceServicesImpl implements BalanceServices {

    private final AccountRepo accountRepo;
    private final ProcessedTransactionRepo transactionRepo;


    @Override
    @Transactional
    public TransactionResponseDto withdraw(WithdrawRequestDto withdrawRequestDto) {
        if(withdrawRequestDto == null)
            throw new NullPointerException("Null request sent!!");

        //check if the transaction is already processed for idempotency
        if(transactionRepo.existsByProcessedTransactionId(withdrawRequestDto.getTransactionId()))
            throw new IllegalArgumentException("Transaction: " + withdrawRequestDto.getTransactionId() + " is already processed!!");

        if (withdrawRequestDto.getAmount() == null ||
                withdrawRequestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Invalid withdrawal amount");


        //check if received JSON body is for different transaction
        if(withdrawRequestDto.getTransactionType() != TransactionType.DEBIT)
            throw new IllegalArgumentException("Invalid Transaction Type for this method!!");

        //check if the accountNumber exists in the account database
        Account account = accountRepo.findByAccountNumber(withdrawRequestDto.getFromAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account does not exist!"));

        // check if withdraw amount is greater than the current balance
        updateLimit(account);
        if (account.getBalance().compareTo(withdrawRequestDto.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient Funds!");
        }

        //check if daily withdrawal limit is already exceeded
        if(account.getAccountDetails().getDailyWithdrawalLimit().compareTo(account.getTotalWithdrawToday().add(withdrawRequestDto.getAmount())) < 0)
            throw new IllegalArgumentException("Exceeded Daily Withdrawal Limit!");

        //check if the per-transaction limit is exceeded
        if(account.getAccountDetails().getPerTransactionLimit().compareTo(withdrawRequestDto.getAmount()) < 0)
            throw new IllegalArgumentException("Per-Transaction Limit Exceeded!");

        account.setBalance(account.getBalance()
                .subtract(withdrawRequestDto.getAmount()));
        account.setTotalWithdrawToday(account.getTotalWithdrawToday().add(withdrawRequestDto.getAmount()));
        accountRepo.save(account);
        transactionRepo.save(ProcessedTransaction.builder()
                        .processedTransactionId(withdrawRequestDto.getTransactionId())
                        .transactionType(withdrawRequestDto.getTransactionType())
                        .amount(withdrawRequestDto.getAmount())
                .build());

        return TransactionResponseDto.builder()
                .toAccountNumber(null)
                .fromAccountNumber(account.getAccountNumber())
                .amount(withdrawRequestDto.getAmount())
                .transactionType(withdrawRequestDto.getTransactionType())
                .transactionId(withdrawRequestDto.getTransactionId())
                .transactionDate(LocalDateTime.now())
                .build();
    }

    @Override
    public TransactionResponseDto deposit(DepositRequestDto depositRequestDto) {
        return null;
    }

    @Override
    public TransactionResponseDto transfer(TransferRequestDto transferRequestDto) {
        return null;
    }

    private void updateLimit(Account account) {
        if (account.getLastTransactionDate() == null || account.getLastTransactionDate().isBefore(LocalDate.now())) {
            account.setTotalWithdrawToday(BigDecimal.ZERO);
            account.setLastTransactionDate(LocalDate.now());
        }
    }


}
