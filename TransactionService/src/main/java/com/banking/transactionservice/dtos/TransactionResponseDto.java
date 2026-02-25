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
    private String toAccountNumber;
    private Long toUserId;
    private String fromAccountNumber;
    private Long fromUserId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
