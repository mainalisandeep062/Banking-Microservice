package com.banking.accountservice.dtos.serviceSpecific;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceUpdateRequest {
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String transactionId;
}

enum TransactionType {
    CREDIT,
    DEBIT,
    TRANSFER
}
