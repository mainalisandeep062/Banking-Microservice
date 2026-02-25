package com.banking.accountservice.dtos.mirror.transaction;

import com.banking.accountservice.enums.TransactionStatus;
import com.banking.accountservice.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto implements Serializable {
    private Long transactionId;
    private String toAccountNumber;
    private Long toUserId;
    private String fromAccountNumber;
    private Long fromUserId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus status;
}
