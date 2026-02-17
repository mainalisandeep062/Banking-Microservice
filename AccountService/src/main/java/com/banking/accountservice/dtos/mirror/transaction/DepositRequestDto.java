package com.banking.accountservice.dtos.mirror.transaction;

import com.banking.accountservice.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequestDto {
    private BigDecimal amount;
    private String toAccountNumber;
    private TransactionType transactionType;
    private Long transactionId;
}
