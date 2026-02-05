package com.banking.accountservice.dtos.serviceSpecific;

import com.banking.accountservice.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionResponseDto {
    private String toAccountNumber;
    private String fromAccountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String transactionId;
}
