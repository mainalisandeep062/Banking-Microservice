package com.banking.accountservice.services.impl;

import com.banking.accountservice.dtos.mirror.transaction.DepositRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.TransferRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.WithdrawRequestDto;
import com.banking.accountservice.enums.Status;
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


@Service
@RequiredArgsConstructor
public class BalanceServicesImpl implements BalanceServices {

    private final AccountRepo accountRepo;
    private final ProcessedTransactionRepo transactionRepo;


    @Override
    @Transactional
    public String withdraw(WithdrawRequestDto withdrawRequestDto) {
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

        if(!account.getStatus().equals(Status.ACTIVE))
            throw new IllegalArgumentException("The account is not active!!");

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

        return "SUCCESS";
    }

    @Override
    @Transactional
    public String deposit(DepositRequestDto depositRequestDto) {
        if(depositRequestDto == null)
            return null;

        if (depositRequestDto.getAmount() == null ||
                depositRequestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Deposit amount must be greater than zero");

        if (depositRequestDto.getTransactionId() == null ||
                depositRequestDto.getTransactionId() == 0)
            throw new IllegalArgumentException("Transaction ID is required");

        if (transactionRepo.existsByProcessedTransactionId(depositRequestDto.getTransactionId()))
            throw new IllegalArgumentException(
                    "Transaction " + depositRequestDto.getTransactionId() + " has already been processed");

        //check if received JSON body is for different transaction
        if(depositRequestDto.getTransactionType() != TransactionType.CREDIT)
            throw new IllegalArgumentException("Invalid Transaction Type for this method!!");

        //Check if the Account exists and is Active
        Account account = accountRepo.findByAccountNumber(depositRequestDto.getToAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Account does not exist!"));
        if(!account.getStatus().equals(Status.ACTIVE))
            throw new IllegalArgumentException("The account is currently " + account.getStatus());

        account.setBalance(account.getBalance().add(depositRequestDto.getAmount()));
        accountRepo.save(account);

        transactionRepo.save(ProcessedTransaction.builder()
                        .processedTransactionId(depositRequestDto.getTransactionId())
                        .transactionType(TransactionType.CREDIT)
                        .amount(depositRequestDto.getAmount())
                        .build());

        return "SUCCESS";
    }

    @Override
    @Transactional
    public String transfer(TransferRequestDto transferRequestDto) {

        // 1. Validate request
        if (transferRequestDto == null)
            throw new IllegalArgumentException("Transfer request cannot be null");
        if (transferRequestDto.getAmount() == null || transferRequestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        if (transferRequestDto.getTransactionId() == null || transferRequestDto.getTransactionId() == 0)
            throw new IllegalArgumentException("Transaction ID is required");

        if (transferRequestDto.getTransactionType() != TransactionType.TRANSFER)
            throw new IllegalArgumentException("Invalid transaction type for transfer");

        // 2. Idempotency check
        if (transactionRepo.existsByProcessedTransactionId(transferRequestDto.getTransactionId())) {
            throw new IllegalArgumentException(
                    "Transaction " + transferRequestDto.getTransactionId() + " has already been processed");
        }

        String fromAccountNumber = transferRequestDto.getFromAccountNumber();
        String toAccountNumber = transferRequestDto.getToAccountNumber();

        // 3. Fetch accounts with locks
        // Lock order by account number to prevent deadlocks
        Account firstAccountToLock;
        Account secondAccountToLock;

        if (fromAccountNumber.compareTo(toAccountNumber) < 0) {
            firstAccountToLock = accountRepo.findByAccountNumber(fromAccountNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Sender account does not exist"));
            secondAccountToLock = accountRepo.findByAccountNumber(toAccountNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Receiver account does not exist"));
        } else {
            firstAccountToLock = accountRepo.findByAccountNumber(toAccountNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Receiver account does not exist"));
            secondAccountToLock = accountRepo.findByAccountNumber(fromAccountNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Sender account does not exist"));
        }

        Account sender = firstAccountToLock.getAccountNumber().equals(fromAccountNumber) ? firstAccountToLock : secondAccountToLock;
        Account receiver = firstAccountToLock.getAccountNumber().equals(toAccountNumber) ? firstAccountToLock : secondAccountToLock;

        // 4. Validate account status
        if(!sender.getStatus().equals(Status.ACTIVE))
            throw new IllegalArgumentException("Sender account is inactive/closed");
        if(!receiver.getStatus().equals(Status.ACTIVE))
            throw new IllegalArgumentException("Receiver account is inactive/closed");

        // 5. Validate withdrawal rules
        updateLimit(sender); // reset daily limit if needed

        if (sender.getBalance().compareTo(transferRequestDto.getAmount()) < 0)
            throw new IllegalArgumentException("Insufficient funds in sender account");

        if (sender.getAccountDetails().getDailyWithdrawalLimit()
                .compareTo(sender.getTotalWithdrawToday().add(transferRequestDto.getAmount())) < 0)
            throw new IllegalArgumentException("Exceeded daily withdrawal limit");

        if (sender.getAccountDetails().getPerTransactionLimit()
                .compareTo(transferRequestDto.getAmount()) < 0)
            throw new IllegalArgumentException("Per-transaction limit exceeded");

        // 6. Perform balance updates
        sender.setBalance(sender.getBalance().subtract(transferRequestDto.getAmount()));
        sender.setTotalWithdrawToday(sender.getTotalWithdrawToday().add(transferRequestDto.getAmount()));

        receiver.setBalance(receiver.getBalance().add(transferRequestDto.getAmount()));

        accountRepo.save(sender);
        accountRepo.save(receiver);

        // 7. Save transaction for auditing
        transactionRepo.save(
                ProcessedTransaction.builder()
                        .processedTransactionId(transferRequestDto.getTransactionId())
                        .transactionType(TransactionType.TRANSFER)
                        .amount(transferRequestDto.getAmount())
                        .build()
        );
        return "SUCCESS";
    }


    private void updateLimit(Account account) {
        if (account.getLastTransactionDate() == null || account.getLastTransactionDate().isBefore(LocalDate.now())) {
            account.setTotalWithdrawToday(BigDecimal.ZERO);
            account.setLastTransactionDate(LocalDate.now());
            accountRepo.save(account);
        }
    }


}
