package com.banking.accountservice.dtos.mirror.transaction;

import com.banking.accountservice.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDto {
    private String toAccountNumber;
    private String fromAccountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String transactionId;
}
