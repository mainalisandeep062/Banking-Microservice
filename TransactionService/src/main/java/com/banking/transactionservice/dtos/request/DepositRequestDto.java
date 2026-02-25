package com.banking.transactionservice.dtos.request;

import com.banking.transactionservice.enums.TransactionType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepositRequestDto {
    private String toAccountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private Long transactionId;
    private Long toUserId;
}
