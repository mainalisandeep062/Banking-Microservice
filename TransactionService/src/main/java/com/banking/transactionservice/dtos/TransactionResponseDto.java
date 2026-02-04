package com.banking.transactionservice.dtos;

import com.banking.transactionservice.enums.TransactionStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDto {
    private String transactionId;
    private TransactionStatus status;
    private String message;
    private LocalDateTime timestamp;
}
