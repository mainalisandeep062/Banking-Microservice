package com.banking.accountservice.dtos.mirror.transaction;

import com.banking.accountservice.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDto {
    private String toAccountNumber;
    private String fromAccountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String transactionId;
    private LocalDateTime transactionDate;
}
