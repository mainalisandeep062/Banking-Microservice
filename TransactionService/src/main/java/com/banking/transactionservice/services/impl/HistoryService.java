package com.banking.transactionservice.services.impl;

import com.banking.transactionservice.clientFeign.AccountClient;
import com.banking.transactionservice.dtos.TransactionResponseDto;
import com.banking.transactionservice.dtos.external.AccountResponseDto;
import com.banking.transactionservice.dtos.external.HistoryResponseDto;
import com.banking.transactionservice.entities.Transaction;
import com.banking.transactionservice.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final TransactionRepo transactionRepo;
    private final AccountClient accountClient;

    public HistoryResponseDto getHistoryByAccountNumber(String accountNumber, int pageNumber) {
        AccountResponseDto feignResponse = accountClient.getAccountByAccountNumber(accountNumber).getBody();

        Pageable pageable = PageRequest.of(pageNumber, 10);
        Page<TransactionResponseDto> transactions = transactionRepo
                .findTransactionByAccount(accountNumber, pageable)
                .map(this::toDto);

        return HistoryResponseDto.builder()
                .accountNumber(feignResponse.getAccountNumber())
                .accountHolderName(feignResponse.getAccountHolderName())
                .createdDate(feignResponse.getCreatedDate())
                .transactions(transactions.toList())
                .build();
    }

    public TransactionResponseDto toDto(Transaction txn){
        return  TransactionResponseDto.builder()
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
