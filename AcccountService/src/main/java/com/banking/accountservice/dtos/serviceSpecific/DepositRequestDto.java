package com.banking.accountservice.dtos.serviceSpecific;

import com.banking.accountservice.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequestDto {
    private String toAccountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String transactionId;
}
