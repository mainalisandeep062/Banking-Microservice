package com.banking.transactionservice.services.impl;

import com.banking.transactionservice.clientFeign.AccountClient;
import com.banking.transactionservice.dtos.TransactionResponseDto;
import com.banking.transactionservice.dtos.request.DepositRequestDto;
import com.banking.transactionservice.dtos.request.TransferRequestDto;
import com.banking.transactionservice.dtos.request.WithdrawRequestDto;
import com.banking.transactionservice.entities.Transaction;
import com.banking.transactionservice.enums.TransactionStatus;
import com.banking.transactionservice.repo.TransactionRepo;
import com.banking.transactionservice.services.TransactionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServicesImpl implements TransactionServices {

    private final TransactionRepo transactionRepo;
    private final AccountClient accountClient;

    @Override
    public TransactionResponseDto withdraw(WithdrawRequestDto withdrawRequestDto) {
        if(withdrawRequestDto == null)
            throw new IllegalArgumentException("Empty request. Can't withdraw!!!");
        Transaction transaction = Transaction.builder()
                .fromAccountNumber(withdrawRequestDto.getFromAccountNumber())
                .transactionType(withdrawRequestDto.getTransactionType())
                .amount(withdrawRequestDto.getAmount())
                .status(TransactionStatus.PENDING)
                .build();
        transactionRepo.save(transaction);
        withdrawRequestDto.setTransactionId(transaction.getTransactionId());

        try{
            String response = accountClient.withdraw(withdrawRequestDto).getBody();

            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDateTime.now());

        }catch(Exception e){
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepo.save(transaction);

            throw new RuntimeException("Transaction Failed: "+e.getMessage());
        }
        Transaction savedTxn = transactionRepo.save(transaction);
        return toDto(savedTxn);
    }

    @Override
    public TransactionResponseDto deposit(DepositRequestDto depositRequestDto) {
        if(depositRequestDto == null)
            throw new IllegalArgumentException("Empty request. Can't deposit!!!");
        Transaction transaction = Transaction.builder()
                .toAccountNumber(depositRequestDto.getToAccountNumber())
                .transactionType(depositRequestDto.getTransactionType())
                .amount(depositRequestDto.getAmount())
                .status(TransactionStatus.PENDING)
                .build();
        transactionRepo.save(transaction);

        depositRequestDto.setTransactionId(transaction.getTransactionId());

        try{
            String response = accountClient.deposit(depositRequestDto).getBody();

            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDateTime.now());

        }catch(Exception e){
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepo.save(transaction);

            throw new RuntimeException("Transaction Failed: " + e.getMessage());
        }
        Transaction savedTxn = transactionRepo.save(transaction);
        return toDto(savedTxn);
    }

    @Override
    public TransactionResponseDto transfer(TransferRequestDto transferRequestDto) {
        if(transferRequestDto == null)
            throw new IllegalArgumentException("Empty request. Can't transfer!!!");

        Transaction transaction = Transaction.builder()
                .toAccountNumber(transferRequestDto.getToAccountNumber())
                .fromAccountNumber(transferRequestDto.getFromAccountNumber())
                .amount(transferRequestDto.getAmount())
                .transactionType(transferRequestDto.getTransactionType())
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepo.save(transaction);
        transferRequestDto.setTransactionId(transaction.getTransactionId());

        try{
            String response = accountClient.transfer(transferRequestDto).getBody();

            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDateTime.now());

        }catch(Exception e){
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepo.save(transaction);

            throw new RuntimeException("Transaction Failed: "+e.getMessage());
        }
        Transaction savedTxn = transactionRepo.save(transaction);
        return toDto(savedTxn);
    }

    public TransactionResponseDto toDto(Transaction txn){
        return TransactionResponseDto.builder()
                .transactionId(txn.getTransactionId())
                .fromAccountNumber(txn.getFromAccountNumber())
                .toAccountNumber(txn.getToAccountNumber())
                .transactionType(txn.getTransactionType())
                .amount(txn.getAmount())
                .status(txn.getStatus())
                .createdAt(txn.getCreatedAt())
                .completedAt(txn.getCompletedAt())
                .build();
    }
}
