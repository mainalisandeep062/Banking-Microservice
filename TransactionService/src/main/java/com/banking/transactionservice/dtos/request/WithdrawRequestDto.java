package com.banking.transactionservice.dtos.request;

import com.banking.transactionservice.enums.TransactionType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawRequestDto {
    private String fromAccountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;
    private Long transactionId;
}
