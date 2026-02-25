package com.banking.accountservice.dtos.mirror.transaction;

import com.banking.accountservice.enums.TransactionType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TransferRequestDto{
    private String toAccountNumber;
    private String fromAccountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private Long transactionId;
    private Long toUserId;
    private Long fromUserId;
}
