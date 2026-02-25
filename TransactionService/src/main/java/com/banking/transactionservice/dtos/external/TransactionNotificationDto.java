package com.banking.transactionservice.dtos.external;

import com.banking.transactionservice.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionNotificationDto implements Serializable {
    private Long transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private Long  fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private TransactionType transactionType;
}
