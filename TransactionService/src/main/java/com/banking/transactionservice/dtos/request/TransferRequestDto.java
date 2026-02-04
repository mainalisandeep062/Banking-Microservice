package com.banking.transactionservice.dtos.request;

import com.banking.transactionservice.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDto {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
}
