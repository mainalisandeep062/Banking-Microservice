package com.banking.transactionservice.dtos;

import com.banking.transactionservice.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceUpdateDto {
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String transactionId;
}
