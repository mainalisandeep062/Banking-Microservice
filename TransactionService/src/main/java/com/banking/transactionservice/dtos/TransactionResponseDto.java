package com.banking.transactionservice.dtos;

import com.banking.transactionservice.enums.TransactionStatus;
import com.banking.transactionservice.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDto {
    private Long transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private TransactionStatus status;
    private TransactionType transactionType;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
